package com.example.service;

import com.example.entity.Course;
import com.example.entity.ExamCommittee;
import com.example.entity.ThesisProjectSupervision;
import com.example.repository.ThesisProjectSupervisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ThesisProjectSupervisionService {
    @Autowired
    private ThesisProjectSupervisionRepository thesisProjectSupervisionRepository;

    public void save(ThesisProjectSupervision thesisProjectEvaluation) {
        thesisProjectSupervisionRepository.save(thesisProjectEvaluation);
    }
    public ThesisProjectSupervision findByCourseAndExamCommittee(Course course, ExamCommittee examCommittee) {
        return thesisProjectSupervisionRepository.findByCourseAndExamCommittee(course, examCommittee);
    }

    public boolean existsByCourseAndExamCommittee(Course course, ExamCommittee examCommittee) {
        return thesisProjectSupervisionRepository.existsByCourseAndExamCommittee(course, examCommittee);
    }
}
