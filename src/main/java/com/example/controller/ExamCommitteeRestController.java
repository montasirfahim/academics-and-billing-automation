package com.example.controller;

import com.example.entity.Course;
import com.example.entity.ExamCommittee;
import com.example.service.ExamCommitteeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/committee")
public class ExamCommitteeRestController {
    @Autowired
    private ExamCommitteeService examCommitteeService;

    @GetMapping("/api/{id}")
    public ResponseEntity<ExamCommittee> getCommittee(@PathVariable Long id) {
        ExamCommittee committee = examCommitteeService.findCommitteeByCommitteeId(id);
        if (committee == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Return 404 if not found
        }

        HttpHeaders headers = new HttpHeaders();
        System.out.println(committee.getChairman().getName());
        headers.add("Content-Type", "application/json");

        return new ResponseEntity<>(committee, headers, HttpStatus.OK);
    }

    @PutMapping("/api/updateStatus/{id}")
    public ResponseEntity<ExamCommittee> updateCommitteeStatus(@PathVariable Long id, @RequestBody Map<String, Boolean> statusUpdate) {
        Boolean isComplete = statusUpdate.get("isComplete");
        if(isComplete == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            ExamCommittee updatedCommittee = examCommitteeService.updateStatus(id, isComplete);
            return new ResponseEntity<>(updatedCommittee, HttpStatus.OK); // 200 OK
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        }
    }

}
