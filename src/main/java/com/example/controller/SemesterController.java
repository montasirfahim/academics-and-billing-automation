package com.example.controller;
import com.example.entity.ExamCommittee;
import com.example.entity.Semester;
import com.example.service.ExamCommitteeService;
import com.example.service.SemesterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
public class SemesterController {
    @Autowired
    private SemesterService semesterService;
    @Autowired
    private ExamCommitteeService examCommitteeService;

    @PostMapping("/semester/new")
    public String newSemester(@ModelAttribute Semester semester) {
        semesterService.saveSemester(semester);
        return "redirect:/semesters/view";
    }

    @GetMapping("/semester/new")
    public String newSemesterForm(Model model) {
        model.addAttribute("semester", new Semester());
        return "semester_form";
    }

    @GetMapping("/semesters/view")
    public String viewAllSemesters() {
        return "all_semesters";
    }

    @GetMapping("/semester/manage/{semesterId}")
    public String manageSemesters(Model model, @PathVariable("semesterId") Long semesterId) {
        model.addAttribute("semesterId", semesterId);
        Semester semester = semesterService.findSemesterById(semesterId)
                .orElseThrow(() -> new RuntimeException("Semester not found"));
        model.addAttribute("semester", semester);

        List<ExamCommittee> examCommittees = examCommitteeService.findAllBySemesterId(semesterId);
        model.addAttribute("examCommittees", examCommittees);

        List<String> colors = Arrays.asList("#ffff99", "#aa80ff", "#ff0066", "#00e699",  "#FF5733", "#8c7386", "#ffbf00", "#0fda60");
        model.addAttribute("colors", colors);

        return "manage_semester";
    }

}
