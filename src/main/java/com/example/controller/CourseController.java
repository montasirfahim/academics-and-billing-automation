package com.example.controller;

import com.example.entity.Course;
import com.example.entity.Semester;
import com.example.entity.User;
import com.example.service.CourseService;
import com.example.service.SemesterService;
import com.example.service.UserService;
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


    @PostMapping("/course/new")
    public String addNewCourse(@ModelAttribute Course course) {
        courseService.saveCourse(course);
        return "redirect:/courses/view";
    }

    @GetMapping("/course/assign/{id}")
    public String courseAssignForm(@PathVariable("id") Long id, Model model) {
        List<User> users = userService.getInternals();
        model.addAttribute("courseId", id);
        model.addAttribute("courseName", courseService.findById(id).getCourseName());
        model.addAttribute("users", users);
        return "course_assign_form";
    }


    @GetMapping("/course/new")
    public String newCourseForm(Model model) {
        model.addAttribute("course", new Course());
        List<Semester> semesters = semesterService.findAllSemesters();
        model.addAttribute("semesters", semesters);
        return "course_form";
    }

    @GetMapping("/courses/view")
    public String viewCourses() {
        return "all_courses";
    }

    @GetMapping("/course/delete/{id}")
    public String deleteCourse(@PathVariable("id") Long courseId) {
        courseService.deleteCourseById(courseId);
        return "redirect:/courses/view";
    }

    @PostMapping("/api/course/assign/{courseId}/{teacherId}")
    @ResponseBody
    public ResponseEntity<Object> assignCourseTeacher(@PathVariable("courseId") Long courseId, @PathVariable("teacherId") Long teacherId){
        Course course = courseService.findById(courseId);
        User courseTeacher = userService.getUserById(teacherId);
        if(courseTeacher == null || course == null) {
            return new ResponseEntity<>("Course or Teacher not found!", HttpStatus.NOT_FOUND);
        }
        course.setCourseTeacher(courseTeacher);
        courseService.saveCourse(course);
        return new ResponseEntity<>(Map.of("message", "Successfully assigned!"), HttpStatus.OK);
    }

    @PostMapping("/api/courses/view/filter")
    @ResponseBody
    public ResponseEntity<Object> filterCourses(@RequestBody Map<String, String> payload) {
        String customCode = payload.get("customCode");
        String session = payload.get("session");
        Map<Object, Object> map = new HashMap<>();
        if(customCode == null || session == null) {
            map.put("message", "Custom Code or Session Not Found!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
        }
        List<Course> courses = courseService.getFilteredCourses(customCode, session);
        if(courses == null || courses.isEmpty()) {
            map.put("message", "No courses found based on your query!");
            map.put("courses", new ArrayList<>());
            return ResponseEntity.status(HttpStatus.OK).body(map);
        }

        map.put("courses", courses);
        map.put("message", "successfully filtered!");
        return ResponseEntity.status(HttpStatus.OK).body(map);
    }
}
