package com.example.repository;

import com.example.entity.Course;
import com.example.entity.Semester;
import com.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    void deleteById(Long id);

    List<Course> findBySemesterAndSessionOrderByCourseCodeAsc(Semester semester, String session);
    List<Course> findBySemesterAndCourseTeacherOrderByCourseCodeAsc(Semester semester, User courseTeacher);
    List<Course> findBySemesterAndInternalQuesSetterEvaluatorOrExternalQuesSetterEvaluatorOrderByCourseCodeAsc(Semester semester, User internal, User external);

    long countByCourseTeacher(User user);

    long countByCourseTeacherAndCourseType(User user, String type);

    List<Course> findAllByOrderByIdDesc();

    long countByCourseType(String type);

    // List<Course> findBySessionAndSemester(String committeeSession, Semester semester);


}
