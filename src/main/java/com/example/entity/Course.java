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
    @JoinColumn(name = "course_teacher_id", referencedColumnName = "userId" )
    private User courseTeacher;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "external_ques_setter_id", referencedColumnName = "userId")
    private User externalQuesSetterEvaluator;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "internal_ques_setter_id", referencedColumnName = "userId")
    private User internalQuesSetterEvaluator;

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

    public User getCourseTeacher() {
        return courseTeacher;
    }

    public void setCourseTeacher(User courseTeacher) {
        this.courseTeacher = courseTeacher;
    }

    public User getExternalQuesSetterEvaluator() {
        return externalQuesSetterEvaluator;
    }

    public void setExternalQuesSetterEvaluator(User externalTeacher) {
        this.externalQuesSetterEvaluator = externalTeacher;
    }

    public Semester getSemester() {
        return semester;
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
    }

    public User getInternalQuesSetterEvaluator() {
        return internalQuesSetterEvaluator;
    }

    public void setInternalQuesSetterEvaluator(User internalQuesSetterEvaluator) {
        this.internalQuesSetterEvaluator = internalQuesSetterEvaluator;
    }
}

