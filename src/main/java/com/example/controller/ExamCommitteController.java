package com.example.controller;

import com.example.entity.Course;
import com.example.entity.ExamCommittee;
import com.example.entity.Semester;
import com.example.entity.User;
import com.example.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ExamCommitteController {
    @Autowired
    ExamCommitteeService examCommitteeService;
    @Autowired
    UserService userService;
    @Autowired
    private SemesterService semesterService;


    private final PdfService pdfService;
    @Autowired
    private CourseService courseService;

    public ExamCommitteController(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    @GetMapping("/committee/new/{semesterId}")
    public String newCommittee(@PathVariable("semesterId") Long semesterId, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        if(!user.getRole().equals("admin") && !user.getRole().equals("co-admin")) {
            model.addAttribute("status", "Access Denied");
            model.addAttribute("error", "You are not allowed to perform this action");
            return "error_page";
        }

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
    public String createCommittee(@ModelAttribute ExamCommittee examCommittee, @PathVariable Long semesterId, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        if(!user.getRole().equals("admin") && !user.getRole().equals("co-admin")) {
            model.addAttribute("status", "Access Denied");
            model.addAttribute("error", "You are not allowed to perform this action");
            return "error_page";
        }

        Semester semester = semesterService.findSemesterById(semesterId)
                .orElseThrow(() -> new RuntimeException("Semester not found"));

        examCommittee.setSemester(semester);
        examCommittee.setChairman(userService.getUserById(examCommittee.getChairman().getUserId()));
        examCommittee.setInternalMember1(userService.getUserById(examCommittee.getInternalMember1().getUserId()));
        examCommittee.setInternalMember2(userService.getUserById(examCommittee.getInternalMember2().getUserId()));
        examCommittee.setExternalMember1(userService.getUserById(examCommittee.getExternalMember1().getUserId()));

        examCommitteeService.saveCommittee(examCommittee);

        return "redirect:/semester/manage/{semesterId}";
    }

    @GetMapping("/committee/manage/{id}")
    public String manageCommittee(@PathVariable Long id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        ExamCommittee examCommittee = examCommitteeService.findCommitteeByCommitteeId(id);
        if(examCommittee == null) {
            model.addAttribute("status", "404");
            model.addAttribute("error", "Committee not found");
            return "error_page";
        }
        if(!examCommitteeService.checkViewPermission(user, examCommittee)){
            model.addAttribute("status", "Access Denied");
            model.addAttribute("error", "You are not allowed to view this committee. Only committee members or admins have this permission.");
            return "error_page";
        }

        model.addAttribute("committee", examCommittee);

        List<Course> committeeCourses = courseService.findByCommitteeId(id);
        model.addAttribute("committeeCourses",committeeCourses);

        List<User> internals = userService.getInternals();
        List<User> externals = userService.getExternals();
        model.addAttribute("internals", internals);
        model.addAttribute("externals", externals);

        model.addAttribute("committeeId", id);
        return "manage_committee";
    }

    @GetMapping("/committee/update-student/{id}")
    public String updateCommittee(@PathVariable Long id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        ExamCommittee committee = examCommitteeService.findCommitteeByCommitteeId(id);
        if (committee == null) {
            model.addAttribute("status", "Not Found");
            model.addAttribute("error", "Committee not found");
            return "error_page";
        }

        model.addAttribute("committee", committee);
        return "update_committee_student_form";
    }

    @PostMapping("/api/committee/update-student")
    @ResponseBody
    public ResponseEntity<Object> updateCommitteeStudent(@RequestBody Map<String, String> payload, HttpSession session) {
        User user = (User) session.getAttribute("user");
        Map<Object, Object> map = new HashMap<>();
        if (user == null) {
            map.put("message", "Unauthorized: Please login first");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(map);
        }

        String id = payload.get("committeeId");
        String newValue = payload.get("newValue");
        try{
            Long committeeId = Long.parseLong(id);
            Long studentCount = Long.parseLong(newValue);
            ExamCommittee committee = examCommitteeService.findCommitteeByCommitteeId(committeeId);
            if(committee == null || studentCount <= 0) {
                map.put("message", "Committee not found or Invalid student count.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
            }
            if(user.getUserId().equals(committee.getChairman().getUserId()) || user.getRole().equals("admin") || user.getRole().equals("co-admin")) {
                examCommitteeService.updateStudentCount(committee, studentCount);
                map.put("message", "Student count has been updated successfully!");
                return ResponseEntity.status(HttpStatus.OK).body(map);
            }

            map.put("message", "Bad Request: Something went wrong");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);

        }catch (Exception e){
            map.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
        }
    }

}
