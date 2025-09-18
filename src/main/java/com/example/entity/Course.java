package com.example.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;

@Entity
public class Course {
    @EmbeddedId
    private CourseId id;

    private String course_name;
    private int course_credit;

    public Course() {
        this.id = new CourseId();
    }

    public String getCourse_code() {
        return id.getCourser_code();
    }
    public void setCourse_code(String courser_code) {
        id.setCourser_code(courser_code);
    }
    public String getCourse_name() {
        return course_name;
    }
    public void setCourse_name(String course_name) {
        this.course_name = course_name;
    }
    public int getCourse_credit() {
        return course_credit;
    }
    public void setCourse_credit(int course_credit) {
        this.course_credit = course_credit;
    }

    public String getSession() {
        return id.getSession();
    }

    public void setSession(String session) {
        id.setSession(session);
    }
}

