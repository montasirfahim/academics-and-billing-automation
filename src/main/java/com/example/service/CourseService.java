package com.example.service;

import com.example.entity.Course;
import com.example.entity.ExamCommittee;
import com.example.entity.Semester;
import com.example.entity.User;
import com.example.repository.CourseRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    private SemesterService semesterService;
    @Autowired
    private ExamCommitteeService examCommitteeService;

    public void saveCourse(Course course) {
        courseRepository.save(course);
    }

    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    public Course findById(Long id) {
        return courseRepository.findById(id).orElse(null);
    }


    public void deleteCourseById(Long id) {
        courseRepository.deleteById(id);
    }

    public List<Course> getFilteredCourses(String customCode, String session) {
        Semester semester = semesterService.findByCustomSemesterCode(customCode);
        if(semester == null || session == null) {
            return null;
        }
        return courseRepository.findBySemesterAndSession(semester, session);
    }

    public List<Course> findByCommitteeId(Long committeeId) {//committee courses
        ExamCommittee committee = examCommitteeService.findCommitteeByCommitteeId(committeeId);
        if(committee == null) {
            return null;
        }
        String committeeSession = committee.getSession();
        Semester semester = committee.getSemester();
        return courseRepository.findBySemesterAndSession(semester, committeeSession);
    }

    @Transactional
    public Boolean updateQuesSetterAndEvaluator(Long courseId, User internalTeacher, User externalTeacher, ExamCommittee examCommittee) {
        try{
            Optional<Course> courseOptional = courseRepository.findById(courseId);
            if(courseOptional.isPresent()) {
                Course course = courseOptional.get();
                course.setInternalQuesSetterEvaluator(internalTeacher);
                course.setExternalQuesSetterEvaluator(externalTeacher);
                courseRepository.save(course);
                //notify teachers via email -> will implement later
                return true;
            }
            else{
                return false;
            }
        }
        catch(Exception e) {
            System.out.println(e);
            return false;
        }
    }

    public long getTotalCoursesByUser(User user) {
        return courseRepository.countByCourseTeacher(user);
    }
}
