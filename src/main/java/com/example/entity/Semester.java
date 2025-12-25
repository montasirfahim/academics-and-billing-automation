package com.example.entity;
import jakarta.persistence.*;

@Entity
public class Semester {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long semesterId;

    private String customSemesterCode;

    private Integer semesterHeldYear;
    private String semesterHeldMonths;

    private Integer semesterScheduledYear;
    private String semesterScheduledMonths;

    private String semesterParity; //odd-1st semester or even-2nd semester


    public Semester() {}

    public Integer getSemesterHeldYear() {
        return semesterHeldYear;
    }

    public void setSemesterHeldYear(Integer year) {
        this.semesterHeldYear = year;
    }

    public String getSemesterParity() {
        return semesterParity;
    }

    public void setSemesterParity(String semester_parity) {
        this.semesterParity = semester_parity;
    }
    public Long getSemesterId(){
        return semesterId;
    }

    public void setSemesterId(Long semesterId){
        this.semesterId = semesterId;
    }

    public Integer getSemesterScheduledYear() {
        return semesterScheduledYear;
    }

    public void setSemesterScheduledYear(Integer semesterScheduledYear) {
        this.semesterScheduledYear = semesterScheduledYear;
    }

    public String getSemesterHeldMonths() {
        return semesterHeldMonths;
    }

    public void setSemesterHeldMonths(String semesterHeldMonths) {
        this.semesterHeldMonths = semesterHeldMonths;
    }

    public String getSemesterScheduledMonths() {
        return semesterScheduledMonths;
    }

    public void setSemesterScheduledMonths(String semesterScheduledMonths) {
        this.semesterScheduledMonths = semesterScheduledMonths;
    }

    public String getCustomSemesterCode() {
        return customSemesterCode;
    }

    public void setCustomSemesterCode(String customSemesterCode) {
        this.customSemesterCode = customSemesterCode;
    }
}
