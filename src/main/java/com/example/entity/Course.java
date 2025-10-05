package com.example.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;

@Entity
public class Course {
    @EmbeddedId
    private CourseId id;

    private String course_name;
    private double course_credit;
    private String courseType;

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
    public double getCourse_credit() {
        return course_credit;
    }
    public void setCourse_credit(double course_credit) {
        this.course_credit = course_credit;
    }

    public String getSession() {
        return id.getSession();
    }

    public void setSession(String session) {
        id.setSession(session);
    }

    public String getCourseType() {
        return courseType;
    }
    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public CourseId getId() {
        return id;
    }
    public void setId(CourseId id) {
        this.id = id;
    }
}

