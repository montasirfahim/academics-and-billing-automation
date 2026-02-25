package com.example.controller;
import com.example.entity.*;
import com.example.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
public class SemesterController {
    @Autowired
    private SemesterService semesterService;
    @Autowired
    private ExamCommitteeService examCommitteeService;
    @Autowired
    private UserService userService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private ThirdExaminationService thirdExaminationService;
    @Autowired
    private GratuityBillService gratuityBillService;


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

    @GetMapping("/details/{userId}/{semesterId}")
    public String getDetailedInfoPerUserAndSemester(@PathVariable("userId") Long userId, @PathVariable("semesterId") Long semesterId, HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("user");
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        if(!loggedInUser.getRole().equals("admin") && !loggedInUser.getRole().equals("co-admin") && !loggedInUser.getUserId().equals(userId)) {
            model.addAttribute("status", "Access Denied");
            model.addAttribute("error", "You are not allowed to perform this action");
            return "error_page";
        }
        User targetUser = userService.getUserById(userId);
        Semester semester = semesterService.findById(semesterId);
        if(targetUser == null || semester == null) {
            model.addAttribute("status", "Not Found");
            model.addAttribute("error", "Semester or User not found!");
            return "error_page";
        }

        if(targetUser.getRole().equals("admin") || targetUser.getRole().equals("co-admin")) {
            model.addAttribute("status", "Not Found");
            model.addAttribute("error", "Admins are not associated with academic activities.");
            return "error_page";
        }

        List<Course> conductedCourses = courseService.findByCourseTeacherAndSemester(targetUser, semester);
        List<Course> coursesAsSetter = courseService.findBySemesterAndTeacherAsQuesSetter(semester, targetUser);
        List<ThirdExamination> thirdExaminationList = thirdExaminationService.findByExaminerIdAndSemesterId(userId, semesterId);

        List<ExamCommittee> examCommitteeList = examCommitteeService.findAllBySemesterId(semesterId);
        List<CommitteeBillDTO> committeeBillDTOList = new ArrayList<>();
        if(examCommitteeList != null) {
            for(ExamCommittee examCommittee : examCommitteeList){
                double billAmount = gratuityBillService.getTotalBillAmountByUserAndExamCommittee(targetUser, examCommittee);
                String batchName = UtilityService.getBatchNameFromExamCommittee(examCommittee);
                CommitteeBillDTO committeeBillDTO = new CommitteeBillDTO(batchName, examCommittee.getCommitteeId(), examCommittee.getChairman().getName(), billAmount, examCommittee.isResultPublished());
                committeeBillDTOList.add(committeeBillDTO);
            }
        }


        model.addAttribute("conductedCourses", conductedCourses);
        model.addAttribute("coursesAsSetter", coursesAsSetter);
        model.addAttribute("semester", semester);
        model.addAttribute("thirdExaminationList", thirdExaminationList);
        model.addAttribute("teacherName", targetUser.getName());
        model.addAttribute("role", targetUser.getRole());
        model.addAttribute("committeeBillDTOList", committeeBillDTOList);

        return "semester_user_details";
    }

    @GetMapping("/semesters/api/all")
    @ResponseBody
    public ResponseEntity<List<Semester>> getAllSemesters() {
        List<Semester> semesters = semesterService.findAllSemesters();

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(semesters.size()));
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<>(semesters, headers, HttpStatus.OK);
    }

}
