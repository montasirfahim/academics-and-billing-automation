package com.example.repository;

import com.example.entity.Course;
import com.example.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    void deleteById(Long id);

    List<Course> findBySemesterAndSession(Semester semester, String session);
}
