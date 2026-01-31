package com.example.controller;

import com.example.entity.*;
import com.example.service.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
            Semester semester = examCommittee.getSemester();

            List<Course> committeeCourses = courseService.findByCommitteeId(id);

            byte[] pdfBytes = pdfService.createCommitteePdf(examCommittee, semester, committeeCourses);

            // Set response headers
            String filePath = "committee" + id + ".pdf";
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "inline; filename=" + filePath);
            response.setContentLength(pdfBytes.length);

            // Write PDF bytes to response
            response.getOutputStream().write(pdfBytes);
            response.getOutputStream().flush();
        }
        else return;
    }

    //Simple DTO used by the POST endpoint
    public static class TableRequest {
        private List<List<String>> rows;

        public TableRequest() {}

        public List<List<String>> getRows() {
            return rows;
        }

        public void setRows(List<List<String>> rows) {
            this.rows = rows;
        }
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


}
