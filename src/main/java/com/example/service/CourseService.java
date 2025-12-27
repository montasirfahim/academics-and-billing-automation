package com.example.service;

import com.example.entity.Course;
import com.example.entity.Semester;
import com.example.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    private SemesterService semesterService;

    public void saveCourse(Course course) {
        courseRepository.save(course);
    }

    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    public Course findById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found: " + id));
    }


    public void deleteCourseById(Long id) {
        courseRepository.deleteById(id);
    }

    public List<Course> getFilteredCourses(String customCode, String session) {
        Semester semester = semesterService.findByCustomSemesterCode(customCode);
        if(semester == null || session == null) {
            return null;
        }
        return courseRepository.findBySemesterAndSession(semester, session);
    }
}
