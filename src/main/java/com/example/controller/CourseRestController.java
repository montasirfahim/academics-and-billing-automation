package com.example.controller;
import com.example.entity.Course;
import com.example.entity.User;
import com.example.service.CourseService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseRestController {
    @Autowired
    private CourseService courseService;

    @GetMapping("api/all")
    public ResponseEntity<List<Course>> getAllCourses(HttpSession session){
        User user = (User)session.getAttribute("user");
        if(user==null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        List<Course> courses = courseService.findAll();
        if(!user.getRole().equals("admin") && !user.getRole().equals("co-admin")){
            courses = courseService.findAllByCourseTeacher(user);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(courses.size()));
        headers.add("Content-Type", "application/json");

        return new ResponseEntity<>(courses, headers, HttpStatus.OK);
    }
}
