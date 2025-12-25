package com.example.entity;

import jakarta.persistence.*;

@Entity
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    String courseCode;
    String session;
    private String courseName;
    private double courseCredit;
    private String courseType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "semester_id", referencedColumnName = "semesterId")
    private Semester semester;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "internal_teacher_id", referencedColumnName = "userId" )
    private User internalTeacher;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "external_teacher_id", referencedColumnName = "userId")
    private User externalTeacher;

    public Course() {
    }

    public String getCourseCode() {
        return courseCode;
    }
    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }
    public String getCourseName() {
        return courseName;
    }
    public void setCourseName(String course_name) {
        this.courseName = course_name;
    }
    public double getCourseCredit() {
        return courseCredit;
    }
    public void setCourseCredit(double courseCredit) {
        this.courseCredit = courseCredit;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getCourseType() {
        return courseType;
    }
    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public User getInternalTeacher() {
        return internalTeacher;
    }

    public void setInternalTeacher(User internalTeacher) {
        this.internalTeacher = internalTeacher;
    }

    public User getExternalTeacher() {
        return externalTeacher;
    }

    public void setExternalTeacher(User externalTeacher) {
        this.externalTeacher = externalTeacher;
    }

    public Semester getSemester() {
        return semester;
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
    }
}

