package com.example.repository;

import com.example.entity.Course;
import com.example.entity.Semester;
import com.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    void deleteById(Long id);

    List<Course> findBySemesterAndSessionOrderByCourseCodeAsc(Semester semester, String session);
    List<Course> findBySemesterAndCourseTeacherOrderByCourseCodeAsc(Semester semester, User courseTeacher);
    List<Course> findBySemesterAndInternalQuesSetterEvaluatorOrExternalQuesSetterEvaluatorOrderByCourseCodeAsc(Semester semester, User internal, User external);
    List<Course> findBySemesterAndSessionAndCourseTeacherOrderByCourseCodeAsc(Semester semester, String session, User courseTeacher);

    boolean existsByCourseTypeAndSessionAndSemester(String courseType, String session, Semester semester);

    long countByCourseTeacher(User user);

    long countByCourseTeacherAndCourseType(User user, String type);

    List<Course> findAllByOrderByIdDesc();

    long countByCourseType(String type);

    List<Course> findAllByCourseTeacherOrderByCourseCodeAsc(User courseTeacher);

    @Query("SELECT c FROM Course c WHERE c.semester = :semester AND c.session = :session AND c.courseType IN (:type1, :type2)")
    List<Course> findThesisProjectCourseByExamCommittee(@Param("semester") Semester semester, @Param("session") String session,  @Param("type1") String type1, @Param("type2") String type2);

    @Query("SELECT c FROM Course c WHERE c.semester = :semester AND c.session = :session AND c.courseType = :type1")
    List<Course> findTCourseByExamCommitteeAndCourseType(@Param("semester") Semester semester, @Param("session") String session,  @Param("type1") String type1);

}
