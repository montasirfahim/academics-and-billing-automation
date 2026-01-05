package com.example.service;

import com.example.entity.*;
import com.example.repository.ThirdExaminationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ThirdExaminationService {
    @Autowired
    private ThirdExaminationRepository thirdExaminationRepository;
    @Autowired
    private ExamCommitteeService examCommitteeService;
    @Autowired
    private UserService userService;
    @Autowired
    private SemesterService semesterService;

    @Transactional
    public boolean saveThirdExamination(ExamCommittee examCommittee, Course course, User examiner, Long scriptsCount) {
        ThirdExamination thirdExamination = new ThirdExamination();

        boolean exists = thirdExaminationRepository.existsByExamCommitteeAndCourseAndExaminer(
                examCommittee, course, examiner
        );

        if (exists) {
            return false;
        }

        thirdExamination.setCourse(course);
        thirdExamination.setExaminer(examiner);
        thirdExamination.setScriptsCount(scriptsCount);
        thirdExamination.setExamCommittee(examCommittee);
        thirdExamination.setSemester(course.getSemester());

        thirdExaminationRepository.save(thirdExamination);
        return true;
    }

    public List<ThirdExamination> findAllThirdExamination() {
        return thirdExaminationRepository.findAll();
    }
    public List<ThirdExamination> findByExamCommitteeId(Long examCommitteeId) {
        ExamCommittee examCommittee = examCommitteeService.findCommitteeByCommitteeId(examCommitteeId);
        if (examCommittee != null) {
            return thirdExaminationRepository.findByExamCommittee(examCommittee);
        }
        return null;
    }

    public List<ThirdExamination> findByExaminerIdAndSemesterId(Long userId, Long semesterId) {
        User examiner = userService.getUserById(userId);
        Semester semester = semesterService.findById(semesterId);
        if(examiner != null && semester != null) {
            return thirdExaminationRepository.findByExaminerAndSemester(examiner, semester);
        }
        return null;
    }
}
