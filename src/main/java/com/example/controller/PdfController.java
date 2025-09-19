package com.example.controller;

import com.example.entity.AssignedCourse;
import com.example.entity.ExamCommittee;
import com.example.entity.Semester;
import com.example.service.AssignedCourseService;
import com.example.service.ExamCommitteeService;
import com.example.service.PdfService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Controller
public class PdfController {
    @Autowired
    ExamCommitteeService examCommitteeService;
    @Autowired
    AssignedCourseService assignedCourseService;

    private final PdfService pdfService;

    public PdfController(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    @GetMapping("test/sample_pdf/{id}")
    public void generateCommitteePdf(HttpServletResponse response, @PathVariable Long id) throws IOException {
        ExamCommittee examCommittee = examCommitteeService.findCommitteeByCommitteeId(id);
        Semester semester = examCommittee.getSemester();
        String committeeSession = examCommittee.getSession(); //need to query matched courses for any committee

        List<AssignedCourse> assignedCourses = assignedCourseService.findAllAssignedCourse();

        byte[] pdfBytes = pdfService.createPdf(examCommittee, semester, assignedCourses);

        // Set response headers
        String filePath = "committee" + id + ".pdf";
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=" + filePath);
        response.setContentLength(pdfBytes.length);

        // Write PDF bytes to response
        response.getOutputStream().write(pdfBytes);
        response.getOutputStream().flush();
    }

//    @PostMapping(value = "/generate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_PDF_VALUE)
//    public ResponseEntity<byte[]> generatePdf(@RequestBody TableRequest request) {
//        // Convert List<List<String>> -> List<String[]>
//        List<String[]> rows = request.getRows()
//                .stream()
//                .map(list -> list.toArray(new String[0]))
//                .collect(Collectors.toList());
//
//        byte[] pdfBytes = pdfService.createPdf(rows);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_PDF);
//        // Force download
//        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"report.pdf\"");
//        headers.setContentLength(pdfBytes.length);
//
//        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
//    }

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
}
