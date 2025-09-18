package com.example.entity;
import jakarta.persistence.*;

@Entity
@Table(name = "committee_courses")
public class CommitteeCourse {

    @EmbeddedId
    private CommitteeCourseId id;

    public CommitteeCourse() {}

    public CommitteeCourse(Long committee_id, String course_code, Long semester_id) {
        this.id = new CommitteeCourseId(committee_id, course_code, semester_id);
    }

    public CommitteeCourseId getId() {
        return id;
    }

    public void setId(CommitteeCourseId id) {
        this.id = id;
    }
}