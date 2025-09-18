package com.example.entity;
import jakarta.persistence.*;

@Entity
public class Semester {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long semesterId;
    private Integer semesterHeldYear;
    private Integer semesterScheduledYear;
    private String semesterParity; //odd-1st semester or even-2nd semester

    public Semester(Integer semesterYear,Integer semesterScheduledYear, String semesterParity) {
        this.semesterHeldYear = semesterYear;
        this.semesterScheduledYear = semesterScheduledYear;
        this.semesterParity = semesterParity;
    }
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
}
