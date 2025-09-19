package com.example.service;

import com.example.entity.ExamCommittee;
import com.example.entity.Semester;
import com.example.repository.ExamCommitteeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExamCommitteeService {
    @Autowired
    private ExamCommitteeRepository examCommitteeRepository;

    public List<ExamCommittee> findAllBySemesterId(Long semesterId){
        return examCommitteeRepository.findBySemester_SemesterId(semesterId);
    }

    public void saveCommittee(ExamCommittee examCommittee){
        examCommitteeRepository.save(examCommittee);
    }

    public ExamCommittee findCommitteeByCommitteeId(Long committeeId){
        return examCommitteeRepository.findBycommitteeId(committeeId);
    }

    @Transactional
    public ExamCommittee updateStatus(Long id, boolean isComplete) {
        return examCommitteeRepository.findById(id)
                .map(committee -> {
                    committee.setIsCompleted(isComplete);
                    return examCommitteeRepository.save(committee);
                })
                .orElseThrow(() -> new RuntimeException("Committee not found with id " + id));
    }
}
