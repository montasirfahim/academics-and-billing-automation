package com.example.controller;

import com.example.entity.*;
import com.example.service.CourseService;
import com.example.service.ExamCommitteeService;
import com.example.service.PdfService;
import com.example.service.TourAllowanceBillService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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

    public PdfController(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    @GetMapping("/print/committee/{id}")
    public void generateCommitteePdf(HttpServletResponse response, @PathVariable Long id) throws IOException {
        ExamCommittee examCommittee = examCommitteeService.findCommitteeByCommitteeId(id);
        if(examCommittee == null) {
            return;
        }
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

    // Simple DTO used by the POST endpoint
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
    public void generateTadaPdf(HttpServletResponse response, @PathVariable Long id, HttpSession session) throws IOException {
        User user = (User) session.getAttribute("user");
        TourAllowanceBill bill = tourAllowanceBillService.findById(id);
        if(bill == null) {
            return;
        }

        byte[] pdfBytes = pdfService.createTaDaBillPdf(bill);

        // Set response headers
        String filePath = "tada_bill" + id + ".pdf";
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=" + filePath);
        response.setContentLength(pdfBytes.length);

        // Write PDF bytes to response
        response.getOutputStream().write(pdfBytes);
        response.getOutputStream().flush();

    }
}
