package com.example.controller;

import com.example.entity.*;
import com.example.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.support.ResourceTransactionManager;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CourseController {
    @Autowired
    private CourseService courseService;

    @Autowired
    private SemesterService semesterService;
    @Autowired
    private UserService userService;
    @Autowired
    private ThirdExaminationService thirdExaminationService;
    @Autowired
    private ExamCommitteeService examCommitteeService;


    @PostMapping("/course/new")
    public String addNewCourse(@ModelAttribute Course course, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        if(!user.getRole().equals("admin") && !user.getRole().equals("co-admin")) {
            model.addAttribute("status", "Access Denied");
            model.addAttribute("error", "You are not allowed to perform this action");
            return "error_page";
        }
        courseService.saveCourse(course);
        return "redirect:/courses/view";
    }

    @GetMapping("/course/assign/{id}")
    public String courseAssignForm(@PathVariable("id") Long id, Model model, HttpSession session) {
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
        model.addAttribute("courseId", id);
        model.addAttribute("courseName", courseService.findById(id).getCourseName());
        model.addAttribute("users", users);
        return "course_assign_form";
    }


    @GetMapping("/course/new")
    public String newCourseForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        if(!user.getRole().equals("admin") && !user.getRole().equals("co-admin")) {
            model.addAttribute("status", "Access Denied");
            model.addAttribute("error", "You are not allowed to perform this action");
            return "error_page";
        }

        model.addAttribute("course", new Course());
        List<Semester> semesters = semesterService.findAllSemesters();
        model.addAttribute("semesters", semesters);
        return "course_form";
    }

    @GetMapping("/courses/view")
    public String viewCourses(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        if(!user.getRole().equals("admin") && !user.getRole().equals("co-admin")) {
            model.addAttribute("visitor", "faculty");
        }
        else{
            model.addAttribute("visitor", "admin");
        }
        return "all_courses";
    }

    @GetMapping("/course/delete/{id}")
    public String deleteCourse(@PathVariable("id") Long courseId, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        if(!user.getRole().equals("admin") && !user.getRole().equals("co-admin")) {
            model.addAttribute("status", "Access Denied");
            model.addAttribute("error", "You are not allowed to perform this action");
            return "error_page";
        }

        courseService.deleteCourseById(courseId);
        return "redirect:/courses/view";
    }

    @PutMapping("/api/course/assign/{courseId}/{teacherId}")
    @ResponseBody
    public ResponseEntity<Object> assignCourseTeacher(@PathVariable("courseId") Long courseId, @PathVariable("teacherId") Long teacherId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return new ResponseEntity<>(Map.of("message", "Please login first!"), HttpStatus.UNAUTHORIZED);
        }
        if(!user.getRole().equals("admin") && !user.getRole().equals("co-admin")) {
                return new ResponseEntity<>(Map.of("message", "You are not allowed to perform this action!"), HttpStatus.FORBIDDEN);
        }

        Course course = courseService.findById(courseId);
        User courseTeacher = userService.getUserById(teacherId);
        if(courseTeacher == null || course == null) {
            return new ResponseEntity<>("Course or Teacher not found!", HttpStatus.NOT_FOUND);
        }

        ExamCommittee examCommittee = examCommitteeService.findCommitteeBySemesterAndSession(course.getSemester(), course.getSession());
        if(examCommittee != null && examCommittee.isResultPublished()){
            return new ResponseEntity<>("Result of this course has already published! You can't modify anything.", HttpStatus.CONFLICT);
        }
        if(course.getCourseType().equals("Industrial Visit") || course.getCourseType().equals("Thesis") || course.getCourseType().equals("Project") || course.getCourseType().equals("Viva Voce")) {
           return new ResponseEntity<>("Bad Request: It's Not a Theory or Lab Course! \nThis type of course does not require course teacher.", HttpStatus.BAD_REQUEST);
        }

        course.setCourseTeacher(courseTeacher);
        courseService.saveCourse(course);
        return new ResponseEntity<>(Map.of("message", "Successfully assigned!"), HttpStatus.OK);
    }

    @PostMapping("/api/courses/view/filter")
    @ResponseBody
    public ResponseEntity<Object> filterCourses(@RequestBody Map<String, String> payload, HttpSession httpSession) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            return new ResponseEntity<>(Map.of("message", "Please login first!"), HttpStatus.UNAUTHORIZED);
        }
        String customCode = payload.get("customCode");
        String session = payload.get("session");
        Map<Object, Object> map = new HashMap<>();
        if(customCode == null || session == null) {
            map.put("message", "Custom Code or Session Not Found!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
        }
        List<Course> courses = courseService.getFilteredCourses(customCode, session, user);
        if(courses == null || courses.isEmpty()) {
            map.put("message", "No courses found based on your query!");
            map.put("courses", new ArrayList<>());
            return ResponseEntity.status(HttpStatus.OK).body(map);
        }

        map.put("courses", courses);
        map.put("message", "successfully filtered!");
        return ResponseEntity.status(HttpStatus.OK).body(map);
    }

    @GetMapping("/details/{userId}/{semesterId}")
    public String getDetailedCourseInfoPerUserAndSemester(@PathVariable("userId") Long userId, @PathVariable("semesterId") Long semesterId, HttpSession session, Model model) {
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

        model.addAttribute("conductedCourses", conductedCourses);
        model.addAttribute("coursesAsSetter", coursesAsSetter);
        model.addAttribute("semester", semester);
        model.addAttribute("thirdExaminationList", thirdExaminationList);
        model.addAttribute("teacherName", targetUser.getName());
        model.addAttribute("role", targetUser.getRole());

        return "semester_user_details";
    }
}
