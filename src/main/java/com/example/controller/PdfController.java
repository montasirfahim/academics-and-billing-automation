package com.example.controller;

import com.example.entity.*;
import com.example.service.*;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.PdfMerger;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Controller
public class PdfController {
    @Autowired
    ExamCommitteeService examCommitteeService;

    private final PdfService pdfService;

    @Autowired
    private CourseService courseService;
    @Autowired
    private TourAllowanceBillService tourAllowanceBillService;
    @Autowired
    private UserService userService;
    @Autowired
    private GratuityBillService gratuityBillService;

    public PdfController(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    @GetMapping("/print/committee/{id}")
    public void generateCommitteePdf(HttpServletResponse response, @PathVariable Long id, HttpSession session) throws IOException {
        User user = (User) session.getAttribute("user");
        if(user == null) {
            return;
        }

        ExamCommittee examCommittee = examCommitteeService.findCommitteeByCommitteeId(id);
        if(examCommittee == null) {
            return;
        }

        if(user.getRole().equals("admin") || user.getRole().equals("co-admin") || examCommitteeService.isMember(user, examCommittee)) {
            byte[] pdfBytes = pdfService.createCommitteePdf(examCommittee);

            String filePath = "committee" + id + ".pdf";
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "inline; filename=" + filePath);
            response.setContentLength(pdfBytes.length);

            response.getOutputStream().write(pdfBytes);
            response.getOutputStream().flush();
        }
        else return;
    }


    @GetMapping("/print/tada/{id}")
    public void generateTadaBillPdf(HttpServletResponse response, @PathVariable Long id, HttpSession session) throws IOException {
        User user = (User) session.getAttribute("user");
        if(user == null){
            return;
        }
        TourAllowanceBill bill = tourAllowanceBillService.findById(id);
        if(bill == null) {
            return;
        }
        if(!user.getRole().equals("admin") && !user.getRole().equals("co-admin") && !user.getUserId().equals(bill.getUser().getUserId())){
            return;
        }

        byte[] pdfBytes = pdfService.createTaDaBillPdf(bill);

        String filePath = "tada_bill" + id + ".pdf";
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=" + filePath);
        response.setContentLength(pdfBytes.length);

        response.getOutputStream().write(pdfBytes);
        response.getOutputStream().flush();

    }

    @GetMapping("/print/tada/report/{id}")
    public void generateTadaReportPdf(HttpServletResponse response, @PathVariable Long id, HttpSession session) throws IOException {
        User user = (User) session.getAttribute("user");
        if(user == null){
            return;
        }
        TourAllowanceBill bill = tourAllowanceBillService.findById(id);
        if(bill == null) {
            return;
        }

        if(!user.getRole().equals("admin") && !user.getRole().equals("co-admin")){ //only for admin. && !user.getUserId().equals(bill.getUser().getUserId())
            return;
        }

        byte[] pdfBytes = pdfService.createTaDaReportPdf(bill);


        String filePath = "tada_report" + id + ".pdf";
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=" + filePath);
        response.setContentLength(pdfBytes.length);

        response.getOutputStream().write(pdfBytes);
        response.getOutputStream().flush();

    }

    @GetMapping("/print-detailed-bill/{userId}/{committeeId}")
    public void generateGratuityBillPdf(HttpServletResponse response, @PathVariable Long userId, @PathVariable Long committeeId, HttpSession session) throws IOException {
        User user = (User) session.getAttribute("user");
        if(user == null){
            return;
        }
        User billUser = userService.getUserById(userId);
        if(billUser == null){
            return;
        }
        ExamCommittee examCommittee = examCommitteeService.findCommitteeByCommitteeId(committeeId);
        if(examCommittee == null) {
            return;
        }
        if(!examCommittee.isResultPublished()){
            return;
        }

        if(user.getRole().equals("admin") || user.getRole().equals("co-admin") || user.getUserId().equals(billUser.getUserId())) {
            List<GratuityBill> gratuityBillList = gratuityBillService.findAllByUserAndExamCommittee(billUser, examCommittee);

            //List<GratuityBill> gratuityBillList2 = new ArrayList<>();

            byte[] pdfBytes = pdfService.createGratuityBillPdf(billUser,examCommittee, gratuityBillList);


            String filePath = "gratuity_bill_" + userId + "_" + committeeId  + ".pdf";

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "inline; filename=" + filePath);
            response.setContentLength(pdfBytes.length);

            response.getOutputStream().write(pdfBytes);
            response.getOutputStream().flush();
        }
    }

    @GetMapping("/bill-summary/download/{committeeId}")
    public void generateBillSummaryPdf(HttpServletResponse response, @PathVariable Long committeeId, HttpSession session) throws IOException {
        User user = (User) session.getAttribute("user");
        if(user == null){
            return;
        }

        ExamCommittee examCommittee = examCommitteeService.findCommitteeByCommitteeId(committeeId);
        if(examCommittee == null) {
            return;
        }
        if(!examCommittee.isResultPublished()){
            return;
        }

        if(!user.getRole().equals("admin") && !user.getRole().equals("co-admin") && !user.getUserId().equals(examCommittee.getChairman().getUserId())){
            return;
        }

        List<Course> committeeCourses = courseService.findByCommitteeId(committeeId);

        byte[] pdfBytes = pdfService.createBillSummary(examCommittee, committeeCourses);
        byte[] classTests = pdfService.createClassTestsSummary(examCommittee, committeeCourses);

        byte[] finalPdf = getFinalPdf(pdfBytes, classTests);

        String filePath = "bill_summary_committee" + committeeId + ".pdf";
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=" + filePath);
        response.setContentLength(finalPdf.length);

        response.getOutputStream().write(finalPdf);
        response.getOutputStream().flush();

    }

    private static byte  [] getFinalPdf(byte[] pdfBytes, byte[] classTests) throws IOException {
        ByteArrayOutputStream mergedStream = new ByteArrayOutputStream();
        PdfDocument mergedDoc = new PdfDocument(new PdfWriter(mergedStream));
        PdfMerger merger = new PdfMerger(mergedDoc);

        PdfDocument firstSource = new PdfDocument(new PdfReader(new ByteArrayInputStream(pdfBytes)));
        merger.merge(firstSource, 1, firstSource.getNumberOfPages());

        PdfDocument secondSource = new PdfDocument(new PdfReader(new ByteArrayInputStream(classTests)));
        merger.merge(secondSource, 1, secondSource.getNumberOfPages());

        firstSource.close();
        secondSource.close();
        mergedDoc.close();


        byte[] finalPdf = mergedStream.toByteArray();
        return finalPdf;
    }


}
