package com.example.repository;

import com.example.entity.ExamCommittee;
import com.example.entity.Semester;
import com.example.entity.User;
import com.example.service.SemesterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExamCommitteeRepository extends JpaRepository<ExamCommittee, Long> {

    List<ExamCommittee> findBySemester_SemesterId(Long semesterId);

    List<ExamCommittee> findAllByOrderByCommitteeIdDesc();

    @Query("SELECT c FROM ExamCommittee c WHERE c.semester.semesterId = :semesterId") //alternative
    List<ExamCommittee> findBySemesterId(@Param("semesterId") Long semesterId);


    ExamCommittee findByCommitteeId(Long committeeId);

    ExamCommittee findBySemesterAndSession(Semester semester, String session);

    long countExamCommitteeByChairman(User user);
    long countExamCommitteeByInternalMember1OrInternalMember2(User user1, User user2);
    long countExamCommitteeByExternalMember1(User user);

    long countExamCommitteeByIsCompleted(boolean b);
}
