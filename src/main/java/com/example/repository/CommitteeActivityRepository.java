package com.example.repository;

import com.example.entity.CommitteeActivity;
import com.example.entity.ExamCommittee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommitteeActivityRepository extends JpaRepository<CommitteeActivity, Long> {

    List<CommitteeActivity> findByExamCommitteeOrderByIdDesc(ExamCommittee examCommittee);

}
