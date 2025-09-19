package com.example.controller;

import com.example.entity.ExamCommittee;
import com.example.entity.Semester;
import com.example.entity.User;
import com.example.service.ExamCommitteeService;
import com.example.service.PdfService;
import com.example.service.SemesterService;
import com.example.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Controller
public class ExamCommitteController {
    @Autowired
    ExamCommitteeService examCommitteeService;
    @Autowired
    UserService userService;
    @Autowired
    private SemesterService semesterService;

    private final PdfService pdfService;
    public ExamCommitteController(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    @GetMapping("/committee/new/{semesterId}")
    public String newCommittee(@PathVariable("semesterId") Long semesterId, Model model) {
        List<User> users = userService.getInternals();
        List<User> externals = userService.getExternals();

        model.addAttribute("users", users);
        model.addAttribute("externals", externals);

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

    @GetMapping("/committee/manage/{id}")
    public String manageCommittee(@PathVariable Long id, Model model) {
        ExamCommittee examCommittee = examCommitteeService.findCommitteeByCommitteeId(id);
        model.addAttribute("committee", examCommittee);
        model.addAttribute("committeeId", id);
        return "manage_committee";
    }

}
