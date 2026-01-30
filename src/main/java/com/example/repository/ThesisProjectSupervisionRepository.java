package com.example.repository;

import com.example.entity.Course;
import com.example.entity.ExamCommittee;
import com.example.entity.ThesisProjectSupervision;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThesisProjectSupervisionRepository extends JpaRepository<ThesisProjectSupervision, Long> {

    ThesisProjectSupervision findByCourseAndExamCommittee(Course course, ExamCommittee examCommittee);
    boolean existsByCourseAndExamCommittee(Course course, ExamCommittee examCommittee);

}
