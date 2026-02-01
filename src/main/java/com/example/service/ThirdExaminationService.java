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
    public boolean saveThirdExamination(ExamCommittee examCommittee, Course course, User examiner, String rawStudentsId, List<String> studentsId, Long scriptsCount) {
        ThirdExamination thirdExamination = new ThirdExamination();

        boolean exists = thirdExaminationRepository.existsByExamCommitteeAndCourseAndExaminer(
                examCommittee, course, examiner
        );

        if (exists) {
            return false;
        }

        thirdExamination.setCourse(course);
        thirdExamination.setExaminer(examiner);
        thirdExamination.setRawStudentsId(rawStudentsId);
        thirdExamination.setStudentsId(studentsId);
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

    public String getStudentsIdById(Long thirdExaminationId){
        ThirdExamination optionalThirdExamination = thirdExaminationRepository.findById(thirdExaminationId).orElse(null);
        if(optionalThirdExamination != null){
            List<String> studentsId = optionalThirdExamination.getStudentsId();
            if(studentsId != null){
               return String.join(", ", studentsId);
            }
            return "Students ID not provided explicitly!";
        }
        return "Error: Students ID not found!";
    }
}
