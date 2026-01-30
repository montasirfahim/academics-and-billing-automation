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
        return courseRepository.findAllByOrderByIdDesc(); //findAll()
    }

    public Course findById(Long id) {
        return courseRepository.findById(id).orElse(null);
    }

    public List<Course> findAllByCourseTeacher(User teacher) {
        return courseRepository.findAllByCourseTeacherOrderByCourseCodeAsc(teacher);
    }
    public void deleteCourseById(Long id) {
        courseRepository.deleteById(id);
    }

    public List<Course> getFilteredCourses(String customCode, String session, User user) {
        Semester semester = semesterService.findByCustomSemesterCode(customCode);
        if(semester == null || session == null) {
            return null;
        }
        if(user.getRole().equals("admin") || user.getRole().equals("co-admin"))
            return courseRepository.findBySemesterAndSessionOrderByCourseCodeAsc(semester, session);
        else
            return courseRepository.findBySemesterAndSessionAndCourseTeacherOrderByCourseCodeAsc(semester, session, user);
    }

    public List<Course> findByCommitteeId(Long committeeId) {//committee courses
        ExamCommittee committee = examCommitteeService.findCommitteeByCommitteeId(committeeId);
        if(committee == null) {
            return null;
        }
        String committeeSession = committee.getSession();
        Semester semester = committee.getSemester();
        return courseRepository.findBySemesterAndSessionOrderByCourseCodeAsc(semester, committeeSession);
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

    public Boolean updateExamineeCount(Course course, Long examineeCount) {
        if(examineeCount == null || course == null) return false;
        try{
            course.setExamineeCount(examineeCount);
            courseRepository.save(course);
            return true;

        }catch(Exception e) {
            System.out.println(e);
            return false;
        }
    }

    public long getTotalCoursesByUser(User user) {
        return courseRepository.countByCourseTeacher(user);
    }

    public long getTotalCoursesByUserAndType(User user, String type) {
        return courseRepository.countByCourseTeacherAndCourseType(user, type);
    }

    public List<Course> findByCourseTeacherAndSemester(User targetUser, Semester semester) {
        return courseRepository.findBySemesterAndCourseTeacherOrderByCourseCodeAsc(semester, targetUser);
    }

    public List<Course> findBySemesterAndTeacherAsQuesSetter(Semester semester, User teacher) {
        return courseRepository.findBySemesterAndInternalQuesSetterEvaluatorOrExternalQuesSetterEvaluatorOrderByCourseCodeAsc(semester, teacher, teacher);
    }

    public long getTotalCoursesCount() {
        return courseRepository.count();
    }

    public long getTotalTheoryCoursesCount() {
        return courseRepository.countByCourseType("Theory");
    }

    public boolean existsByCourseTypeAndSessionAndSemester(String courseType, String session, Semester semester) {
        return courseRepository.existsByCourseTypeAndSessionAndSemester(courseType, session, semester);
    }

    public List<Course> findThesisOrProjectCourseByExamCommittee(ExamCommittee examCommittee) {
        return courseRepository.findThesisProjectCourseByExamCommittee(examCommittee.getSemester(), examCommittee.getSession(), "Thesis", "Project");
    }
}
