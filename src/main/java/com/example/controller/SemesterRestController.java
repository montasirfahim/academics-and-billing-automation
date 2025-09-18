package com.example.controller;

import com.example.entity.ExamCommittee;
import com.example.entity.Semester;
import com.example.service.ExamCommitteeService;
import com.example.service.SemesterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/semesters")
public class SemesterRestController {
    @Autowired
    private SemesterService semesterService;
    @Autowired
    private ExamCommitteeService examCommitteeService;

    @GetMapping("api/all")
    public ResponseEntity<List<Semester>> getAllSemesters() {
        List<Semester> semesters = semesterService.findAllSemesters();

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(semesters.size()));
        headers.add("Content-Type", "application/json");

        return new ResponseEntity<>(semesters, headers, HttpStatus.OK);
    }

    @GetMapping("api/manage/{semesterId}")
    public ResponseEntity<List<ExamCommittee>> getAllExamCommittees(@PathVariable("semesterId") Long semesterId) {
        List<ExamCommittee> committees = examCommitteeService.findAllBySemesterId(semesterId);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(committees.size()));
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<>(committees, headers, HttpStatus.OK);
    }
}
