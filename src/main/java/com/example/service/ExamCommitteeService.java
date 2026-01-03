package com.example.service;

import com.example.entity.Course;
import com.example.entity.ExamCommittee;
import com.example.entity.Semester;
import com.example.entity.User;
import com.example.repository.CourseRepository;
import com.example.repository.ExamCommitteeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExamCommitteeService {
    @Autowired
    private ExamCommitteeRepository examCommitteeRepository;
    @Autowired
    private CourseRepository courseRepository;

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

    public long getTotalCommitteesAsChairman(User user){
        return examCommitteeRepository.countExamCommitteeByChairman(user);
    }

    public long getTotalCommitteesAsInternalMember(User user){
        return examCommitteeRepository.countExamCommitteeByInternalMember1OrInternalMember2(user, user);
    }

    public long getTotalCommitteesAsExternalMember(User user){
        return examCommitteeRepository.countExamCommitteeByExternalMember1(user);
    }

    public long getTotalCommitteesCount() {
        return examCommitteeRepository.count();
    }

    public long getTotalCompletedCommitteesCount() {
        return examCommitteeRepository.countExamCommitteeByIsCompleted(true);
    }

    public long getTotalActiveCommitteesCount() {
        return examCommitteeRepository.countExamCommitteeByIsCompleted(false);
    }

    public Boolean checkQuesModerationEligibility(ExamCommittee examCommittee){
        Semester semester = examCommittee.getSemester();
        String session = examCommittee.getSession();
        List<Course> committeeCourses = courseRepository.findBySemesterAndSessionOrderByCourseCodeAsc(semester, session);
        for(Course course : committeeCourses){
            if(course.getCourseTeacher() == null || course.getInternalQuesSetterEvaluator() == null || course.getExternalQuesSetterEvaluator() == null){
                return false;
            }
        }
        return true;
    }

    public boolean checkViewPermission(User user, ExamCommittee examCommittee) {
        if(user == null){ return false; }
        if(user.getRole().equals("admin") || user.getRole().equals("co-admin")){
            return true;
        }
        return examCommittee.getChairman().getUserId().equals(user.getUserId()) || examCommittee.getInternalMember1().getUserId().equals(user.getUserId()) || examCommittee.getInternalMember2().getUserId().equals(user.getUserId()) || examCommittee.getExternalMember1().getUserId().equals(user.getUserId());
    }

    public void updateStudentCount(ExamCommittee examCommittee, Long studentCount){
        examCommittee.setStudentCount(studentCount);
        examCommitteeRepository.save(examCommittee);
    }
}
