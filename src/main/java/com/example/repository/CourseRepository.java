package com.example.repository;

import com.example.entity.Course;
import com.example.entity.CourseId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, CourseId> {
}
