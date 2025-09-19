package com.example.controller;

import com.example.entity.*;
import com.example.service.AssignedCourseService;
import com.example.service.CourseService;
import com.example.service.SemesterService;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class AssignedCourseController {
    @Autowired
    private UserService userService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private SemesterService semesterService;

    @Autowired
    private AssignedCourseService assignedCourseService;

    @GetMapping("/semester/assign-course/{semesterId}")
    public String assignCourse(Model model, @PathVariable("semesterId") Long semesterId) throws Exception {
        Semester semester = semesterService.findById(semesterId);
        model.addAttribute("semester", semester);

        List<User> users = userService.findAll();

        model.addAttribute("users", users);

        List<Course> courses = courseService.findAll();
        model.addAttribute("courses", courses);

        model.addAttribute("assignedCourse", new AssignedCourse());

        return "assign_course_form";
    }

    @PostMapping("/semester/assign-course/{semesterId}")
    public String saveAssignedCourse(AssignedCourse assignedCourse, @PathVariable Long semesterId, @RequestParam("course_key") String courseKey) {
        Semester semester = semesterService.findSemesterById(semesterId)
                .orElseThrow(() -> new RuntimeException("Semester not found"));


        String[] parts = courseKey.split("\\|"); // ["COURSE123", "2021-22"]
        String courseCode = parts[0];
        String session = parts[1];

        CourseId cid = new CourseId(courseCode, session);
        Course course = courseService.findById(cid);

        assignedCourse.setCourse(course);
        assignedCourse.setSemester(semester);
        assignedCourse.setUser(userService.getUserById(assignedCourse.getUser().getUser_id()));

        assignedCourseService.saveAssignedCourse(assignedCourse);
        return "redirect:/semester/manage/" + semesterId;
    }
}
