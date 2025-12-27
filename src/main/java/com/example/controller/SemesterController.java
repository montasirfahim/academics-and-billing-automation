package com.example.controller;
import com.example.entity.Course;
import com.example.entity.ExamCommittee;
import com.example.entity.Semester;
import com.example.entity.User;
import com.example.service.CourseService;
import com.example.service.ExamCommitteeService;
import com.example.service.SemesterService;
import com.example.service.UserService;
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

        List<String> colors = Arrays.asList(
                "linear-gradient(to right, #f5e6a8, #f0f0e8)",
                "linear-gradient(to right, #c471f5, #fa71cd)",
                "linear-gradient(to right, #ff512f, #dd2476)",
                "linear-gradient(to right, #11998e, #38ef7d)",
                "linear-gradient(to right, #ff8008, #ffc837)",
                "linear-gradient(to right, #8e9eab, #eef2f3)",
                "linear-gradient(to right, #f7971e, #ffd200)",
                "linear-gradient(to right, #00b09b, #96c93d)"
        );

        model.addAttribute("colors", colors);


        return "manage_semester";
    }

    @PostMapping("/semester/delete/{id}")
    public String deleteSemester(@PathVariable("id") Long semesterId) {
        semesterService.deleteBySemesterId(semesterId);
        return "redirect:/semesters/view";
    }

}
