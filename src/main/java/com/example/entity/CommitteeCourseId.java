package com.example.entity;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CommitteeCourseId implements Serializable {
    private Long committee_id;
    private String course_code;
    private Long semester_id;

    public CommitteeCourseId() {}
    public CommitteeCourseId(Long committee_id, String course_code, Long semester_id) {
        this.committee_id = committee_id;
        this.course_code = course_code;
        this.semester_id = semester_id;
    }

    //MUST provide equals() and hashCode() for JPA to work correctly
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommitteeCourseId that = (CommitteeCourseId) o;
        return Objects.equals(committee_id, that.committee_id) &&
                Objects.equals(course_code, that.course_code) &&
                Objects.equals(semester_id, that.semester_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(committee_id, course_code, semester_id);
    }
}