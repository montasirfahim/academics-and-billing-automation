package com.example.controller;

import com.example.entity.*;
import com.example.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
    @Autowired
    private ThirdExaminationService thirdExaminationService;
    @Autowired
    private CommitteeActivityService committeeActivityService;

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
        model.addAttribute("moderationStatus", examCommittee.isModerated());
        model.addAttribute("resultStatus", examCommittee.isResultPublished());

        List<Course> committeeCourses = courseService.findByCommitteeId(id);
        model.addAttribute("committeeCourses",committeeCourses);
        List<Course> thesisOrProjectCourses = courseService.findThesisOrProjectCourseByExamCommittee(examCommittee);
        model.addAttribute("thesisOrProjectCourses",thesisOrProjectCourses);

        List<ThirdExamination> thirdExaminationList = thirdExaminationService.findByExamCommitteeId(id);
        model.addAttribute("thirdExaminationList",thirdExaminationList);

        List<CommitteeActivity> committeeActivityList = committeeActivityService.findByExamCommittee(examCommittee);
        model.addAttribute("committeeActivityList", committeeActivityList);

        List<User> internals = userService.getInternals();
        List<User> externals = userService.getExternals();
        model.addAttribute("internals", internals);
        model.addAttribute("externals", externals);

        model.addAttribute("committeeId", id);
        return "manage_committee";
    }

    @GetMapping("/committee/update-student/{id}")
    public String updateCommitteeStudentForm(@PathVariable Long id, Model model, HttpSession session) {
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
        if(!examCommitteeService.checkEditPermission(user, committee)){
            model.addAttribute("status", "Access Denied");
            model.addAttribute("error", "You are not allowed to perform this action");
            return "error_page";
        }

        model.addAttribute("committee", committee);
        return "update_committee_student_form";
    }

    @PutMapping("/api/committee/update-student")
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
            if(committee.isResultPublished()){
                map.put("message", "Conflict: Result of this Exam Committee has published already. \nYou can't modify this committee right now!");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(map);
            }
            if(examCommitteeService.checkEditPermission(user, committee)) {
                examCommitteeService.updateStudentCount(committee, studentCount);

                CommitteeActivity activity = new CommitteeActivity();
                activity.setExamCommittee(committee);
                activity.setPerformedBy(user);
                activity.setActionTitle("Updating Student Count");
                activity.setDetails("Registered student count of this exam committee has been updated successfully");
                activity.setTimestamp(LocalDate.now());
                committeeActivityService.saveCommitteeActivity(activity);

                map.put("message", "Student count has been updated successfully!");
                return ResponseEntity.status(HttpStatus.OK).body(map);
            }

            map.put("message", "Bad Request: Something went wrong");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);

        }catch (Exception e){
            map.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
        }
    }

    @PutMapping("/api/committee/course/update-examinee")
    @ResponseBody
    public ResponseEntity<Object> updateCommitteeCourseExamineeCount(@RequestBody Map<String, String> payload, HttpSession session) {
        User user = (User) session.getAttribute("user");
        Map<Object, Object> map = new HashMap<>();
        if(user == null) {
            map.put("message", "Unauthorized! Please login first.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(map);
        }

        try{
            Long committeeId = Long.parseLong(payload.get("committeeId"));
            Long courseId = Long.parseLong(payload.get("courseId"));
            Long examineeCount = Long.parseLong(payload.get("examineeCount"));

            ExamCommittee examCommittee = examCommitteeService.findCommitteeByCommitteeId(committeeId);
            Course course = courseService.findById(courseId);

            if(examCommittee == null || course == null){
                map.put("message", "Exam committee or course not found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
            }

            if(!examCommitteeService.checkEditPermission(user, examCommittee)) {
                map.put("message", "Permission denied! Only committee chairman or admins can edit or update anything of an exam committee.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(map);
            }

            if(examCommitteeService.isNotCommitteeCourse(examCommittee, course)) {
                map.put("message", "Data Mismatch! The selected course does not belong to this committee.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(map);
            }

            if(!course.getCourseType().equals("Theory")){
                map.put("message", "Bad Request: It's not a theory course! \nExaminee count is required only for theory courses.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);

            }

            if(examCommittee.isResultPublished()){
                map.put("message", "Conflict: Result of this exam committee has already published!\nYou can't update it right now.");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(map);
            }
            if(examineeCount <= 0){
                map.put("message", "Number of student participated in exam can not be zero or less than zero.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
            }

            if(courseService.updateExamineeCount(course, examineeCount)) {
                CommitteeActivity activity = new CommitteeActivity();
                activity.setExamCommittee(examCommittee);
                activity.setPerformedBy(user);
                activity.setActionTitle("Updating Examinee Count");
                activity.setDetails("Examinee count has been updated successfully for " + course.getCourseName() + " course");
                activity.setTimestamp(LocalDate.now());
                committeeActivityService.saveCommitteeActivity(activity);

                map.put("message", "Examinee count has been updated successfully!");
                return ResponseEntity.status(HttpStatus.OK).body(map);
            }
            else{
                map.put("message", "Bad Request: Something went wrong");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
            }

        }catch (Exception e){
            map.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
        }
    }

    @PutMapping("/api/committee/publish-result/{committeeId}")
    @ResponseBody
    public ResponseEntity<Object> markPublishedCommitteeResult(@PathVariable("committeeId") Long committeeId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        Map<Object, Object> map = new HashMap<>();
        if(user == null) {
            map.put("message", "Unauthorized: Please login first!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(map);
        }
        ExamCommittee examCommittee = examCommitteeService.findCommitteeByCommitteeId(committeeId);
        if(committeeId == null || examCommittee == null) {
            map.put("message", "Bad Request: Committee not found or Invalid committee ID.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
        }
        if(!examCommitteeService.checkEditPermission(user, examCommittee)) {
            map.put("message", "Forbidden: You do not have permission to update anything in this committee.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(map);
        }

        if(examCommittee.isResultPublished()){
            map.put("message", "Conflict: Result is already published!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
        }
        if(!examCommittee.isModerated()){
            map.put("message", "Failed to update result status: Question Moderation of this committee is not completed!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
        }
        if(!examCommitteeService.checkResultPublicationEligibility(examCommittee)){
            map.put("message", "Bad Request: Please update number of students participated in examination for all Theory courses and \nAssign Supervisors for Thesis or Project courses (if exists)!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
        }

        if(examCommitteeService.markResultPublished(user, examCommittee)){
            map.put("message", "Success: Result has been marked as published and all kind of bills related to this committee has been created successfully!");
            return ResponseEntity.status(HttpStatus.OK).body(map);
        }
        else{
            map.put("message", "Internal Server Error: Could not update result status! Please try again...");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
        }
    }

}
