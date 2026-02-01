package com.example.service;

import com.example.entity.CommitteeActivity;
import com.example.entity.ExamCommittee;
import com.example.entity.User;
import com.example.repository.CommitteeActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommitteeActivityService {
    @Autowired
    private CommitteeActivityRepository committeeActivityRepository;

    public void saveCommitteeActivity(CommitteeActivity committeeActivity) {
        committeeActivityRepository.save(committeeActivity);
    }

    public List<CommitteeActivity> findByExamCommittee(ExamCommittee examCommittee) {
        return committeeActivityRepository.findByExamCommitteeOrderByIdDesc(examCommittee);
    }

    public void saveResultPublicationActivity(User performedBy, ExamCommittee examCommittee){
        CommitteeActivity committeeActivity = new CommitteeActivity();
        committeeActivity.setPerformedBy(performedBy);
        committeeActivity.setExamCommittee(examCommittee);
        committeeActivity.setTimestamp(LocalDate.now());
        committeeActivity.setActionTitle("Result Publication");
        committeeActivity.setDetails("Result of this exam committee has been marked as published");
        committeeActivity.setPriority(10);
        committeeActivityRepository.save(committeeActivity);

        CommitteeActivity committeeActivity2 = new CommitteeActivity(committeeActivity);
        committeeActivity2.setActionTitle("Gratuity Bill Generation");
        committeeActivity2.setDetails("Automated gratuity bill generation completed for all teachers assigned to any type of duties within this exam committee");
        committeeActivityRepository.save(committeeActivity2);
    }

}
