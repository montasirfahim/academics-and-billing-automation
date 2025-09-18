package com.example.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CourseId implements Serializable {
    private String courser_code;
    private String session;
    //Must have a no-argument constructor
    public CourseId() { }

    //Implement equals() and hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseId courseId = (CourseId) o;
        return Objects.equals(courser_code, courseId.courser_code) &&
                Objects.equals(session, courseId.session);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courser_code, session);
    }
    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getCourser_code() {
        return courser_code;
    }
    public void setCourser_code(String courser_code) {
        this.courser_code = courser_code;
    }
}