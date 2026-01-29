package com.example.controller;
import com.example.entity.Course;
import com.example.entity.ExamCommittee;
import com.example.entity.Semester;
import com.example.entity.User;
import com.example.service.CourseService;
import com.example.service.ExamCommitteeService;
import com.example.service.SemesterService;
import com.example.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String newSemester(@ModelAttribute Semester semester, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        if(!user.getRole().equals("admin") && !user.getRole().equals("co-admin")) {
            model.addAttribute("status", "Access Denied");
            model.addAttribute("error", "You are not allowed to perform this action");
            return "error_page";
        }

        semesterService.saveSemester(semester);
        return "redirect:/semesters/view";
    }

    @GetMapping("/semester/new")
    public String newSemesterForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        if(!user.getRole().equals("admin") && !user.getRole().equals("co-admin")) {
            model.addAttribute("status", "Access Denied");
            model.addAttribute("error", "You are not allowed to visit this page");
            return "error_page";
        }
        model.addAttribute("semester", new Semester());
        return "semester_form";
    }

    @GetMapping("/semesters/view")
    public String viewAllSemesters(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        return "all_semesters";
    }

    @GetMapping("/semester/manage/{semesterId}")
    public String manageSemesters(Model model, @PathVariable("semesterId") Long semesterId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("semesterId", semesterId);
        Semester semester = semesterService.findSemesterById(semesterId)
                .orElseThrow(() -> new RuntimeException("Semester not found"));
        model.addAttribute("semester", semester);

        List<ExamCommittee> examCommittees = examCommitteeService.findAllBySemesterId(semesterId);
        model.addAttribute("examCommittees", examCommittees);

        List<String> colors = Arrays.asList(
                "linear-gradient(to right, #11998e, #38ef7d)",
                "linear-gradient(to right, #c471f5, #fa71cd)",
                "linear-gradient(to right, #ff512f, #dd2476)",
                "linear-gradient(to right, #ff8008, #ffc837)",
                "linear-gradient(to right, #8e9eab, #eef2f3)",
                "linear-gradient(to right, #f5e6a8, #f0f0e8)",
                "linear-gradient(to right, #f7971e, #ffd200)",
                "linear-gradient(to right, #00b09b, #96c93d)"
        );

        model.addAttribute("colors", colors);


        return "manage_semester";
    }

    @DeleteMapping("/semester/delete/{id}")
    public String deleteSemester(@PathVariable("id") Long semesterId, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        if(!user.getRole().equals("admin") && !user.getRole().equals("co-admin")) {
            model.addAttribute("status", "Access Denied");
            model.addAttribute("error", "You are not allowed to perform this action");
            return "error_page";
        }

        semesterService.deleteBySemesterId(semesterId);
        return "redirect:/semesters/view";
    }

}
