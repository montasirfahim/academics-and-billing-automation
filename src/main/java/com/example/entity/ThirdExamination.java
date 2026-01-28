package com.example.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class ThirdExamination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "committee_id", referencedColumnName = "committeeId")
    private ExamCommittee examCommittee;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id", referencedColumnName = "id")
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id", referencedColumnName = "semesterId")
    private Semester semester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "examiner_id", referencedColumnName = "userId")
    private User examiner;


    private Long scriptsCount;
    private String rawStudentsId;
    private List<String> studentsId;


    public ThirdExamination() {}



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ExamCommittee getExamCommittee() {
        return examCommittee;
    }

    public void setExamCommittee(ExamCommittee examCommittee) {
        this.examCommittee = examCommittee;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Semester getSemester() {
        return semester;
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
    }

    public User getExaminer() {
        return examiner;
    }

    public void setExaminer(User examiner) {
        this.examiner = examiner;
    }

    public Long getScriptsCount() {
        return scriptsCount;
    }

    public void setScriptsCount(Long scriptsCount) {
        this.scriptsCount = scriptsCount;
    }

    public List<String> getStudentsId() {
        return studentsId;
    }

    public void setStudentsId(List<String> studentsId) {
        this.studentsId = studentsId;
    }

    public String getRawStudentsId() {
        return rawStudentsId;
    }

    public void setRawStudentsId(String rawStudentsId) {
        this.rawStudentsId = rawStudentsId;
    }
}
