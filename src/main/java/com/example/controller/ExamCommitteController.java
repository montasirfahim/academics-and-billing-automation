package com.example.controller;

import com.example.entity.ExamCommittee;
import com.example.entity.Semester;
import com.example.entity.User;
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

import java.util.List;

@Controller
public class ExamCommitteController {
    @Autowired
    ExamCommitteeService examCommitteeService;
    @Autowired
    UserService userService;
    @Autowired
    private SemesterService semesterService;

    @GetMapping("/committee/new/{semesterId}")
    public String newCommittee(@PathVariable("semesterId") Long semesterId, Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);

        //Semester semester = semesterService.findSemesterById(semesterId);
        Semester semester = semesterService.findSemesterById(semesterId)
                .orElseThrow(() -> new RuntimeException("Semester not found"));

        model.addAttribute("semester", semester);

        model.addAttribute("committee", new ExamCommittee());

        return "committee_form";
    }

    @PostMapping("/committee/new/{semesterId}")
    public String createCommittee(@ModelAttribute ExamCommittee examCommittee, @PathVariable Long semesterId) {
        Semester semester = semesterService.findSemesterById(semesterId)
                .orElseThrow(() -> new RuntimeException("Semester not found"));

        examCommittee.setSemester(semester);
        examCommittee.setChairman(userService.getUserById(examCommittee.getChairman().getUser_id()));
        examCommittee.setInternalMember1(userService.getUserById(examCommittee.getInternalMember1().getUser_id()));
        examCommittee.setInternalMember2(userService.getUserById(examCommittee.getInternalMember2().getUser_id()));
        examCommittee.setExternalMember1(userService.getUserById(examCommittee.getExternalMember1().getUser_id()));

        examCommitteeService.saveCommittee(examCommittee);

        return "redirect:/semester/manage/{semesterId}";
    }

}
