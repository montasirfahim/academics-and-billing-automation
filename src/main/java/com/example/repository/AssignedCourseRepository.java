package com.example.repository;

import com.example.entity.AssignedCourse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignedCourseRepository extends JpaRepository<AssignedCourse, Long> {
}
