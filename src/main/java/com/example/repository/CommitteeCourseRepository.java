package com.example.repository;

import com.example.entity.CommitteeCourse;
import com.example.entity.CommitteeCourseId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CommitteeCourseRepository extends JpaRepository<CommitteeCourse, CommitteeCourseId> {

    // Use a custom JPQL query for explicit control
    @Query("SELECT c FROM CommitteeCourse c WHERE c.id.committee_id = :committeeId")
    List<CommitteeCourse> findByCommitteeId(@Param("committeeId") Long committeeId);

    // Add the other method for semester as well
    @Query("SELECT c FROM CommitteeCourse c WHERE c.id.committee_id = :committeeId AND c.id.semester_id = :semesterId")
    List<CommitteeCourse> findByCommitteeIdAndSemesterId(@Param("committeeId") Long committeeId, @Param("semesterId") Long semesterId);

}