package com.example.controller;

import com.example.entity.Semester;
import com.example.entity.User;
import com.example.service.CourseService;
import com.example.service.SemesterService;
import com.example.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class BillController {
    @Autowired
    CourseService courseService;
    @Autowired
    UserService userService;
    @Autowired
    SemesterService semesterService;

    @GetMapping("/bill/{userId}/{semId}")
    public String billPage(Model model, @PathVariable("userId") long userId, @PathVariable("semId") long semId, HttpSession session) {
        Semester semester = semesterService.findById(semId);
        User targetUser = userService.getUserById(userId);
        User loggedInUser = (User) session.getAttribute("user");
        if (targetUser == null || semester == null) {
            return "redirect:/home";
        }

        model.addAttribute("semester", semester);
        model.addAttribute("user", targetUser);

        return  "bill_page";
    }
}
