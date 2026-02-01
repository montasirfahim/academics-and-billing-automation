package com.example.repository;

import com.example.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ThirdExaminationRepository extends JpaRepository<ThirdExamination, Long> {

    Optional<ThirdExamination> findById(Long thirdExaminationId);

    List<ThirdExamination> findByExaminer(User examiner);


    List<ThirdExamination> findByExaminerAndSemester(User examiner, Semester semester);

    List<ThirdExamination> findByExamCommittee(ExamCommittee examCommittee);

    boolean existsByExamCommitteeAndCourseAndExaminer(ExamCommittee examCommittee, Course course, User examiner);
    ThirdExamination findByCourseAndExamCommittee(Course course, ExamCommittee examCommittee);
}
