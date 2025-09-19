package com.example.controller;

import com.example.entity.Course;
import com.example.entity.CourseId;
import com.example.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CourseController {
    @Autowired
    private CourseService courseService;

    @PostMapping("/course/new")
    public String addNewCourse(@ModelAttribute Course course) {
        courseService.saveCourse(course);
        return "redirect:/courses/view";
    }
    @GetMapping("/course/new")
    public String newCourseForm(Model model) {
        model.addAttribute("course", new Course());
        return "course_form";
    }

    @GetMapping("/courses/view")
    public String viewCourses() {
        return "all_courses";
    }

    @GetMapping("/course/delete/{id}")
    public String deleteCourse(@PathVariable("id") CourseId course_id) {
        courseService.deleteCourseByCode(course_id);
        return "redirect:/courses/view";
    }
}
