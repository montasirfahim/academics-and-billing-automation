package com.example.service;

import com.example.entity.AssignedCourse;
import com.example.repository.AssignedCourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssignedCourseService {
    @Autowired
    private AssignedCourseRepository assignedCourseRepository;

    public void saveAssignedCourse(AssignedCourse assignedCourse) {
        assignedCourseRepository.save(assignedCourse);
    }

    public List<AssignedCourse> findAllAssignedCourse() {
        return assignedCourseRepository.findAll();
    }
}
