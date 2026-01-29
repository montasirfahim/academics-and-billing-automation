package com.example.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class GratuityBill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String taskName;
    private String examName;
    private String year;
    private String department;
    private List<String> courseCodesOrStuIds;
    private Long numberOfScriptsOrStudents;
    private Long totalDays;
    private Long totalMembers;
    private double creditHour;
    private Long numberOfClassTests;
    private double billRate;
    private double totalBillAmount;
    private String courseCode;

    @JoinColumn(name = "committee_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private ExamCommittee committee;

    @JoinColumn(name = "bill_user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User billUser;

    public GratuityBill() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public List<String> getCourseCodesOrStuIds() {
        return courseCodesOrStuIds;
    }

    public void setCourseCodesOrStuIds(List<String> courseCodes) {
        this.courseCodesOrStuIds = courseCodes;
    }

    public Long getNumberOfScriptsOrStudents() {
        return numberOfScriptsOrStudents;
    }

    public void setNumberOfScriptsOrStudents(Long numberOfScriptsOrStudents) {
        this.numberOfScriptsOrStudents = numberOfScriptsOrStudents;
    }

    public Long getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(Long totalDays) {
        this.totalDays = totalDays;
    }

    public Long getTotalMembers() {
        return totalMembers;
    }

    public void setTotalMembers(Long totalMembers) {
        this.totalMembers = totalMembers;
    }

    public double getCreditHour() {
        return creditHour;
    }

    public void setCreditHour(double creditHour) {
        this.creditHour = creditHour;
    }

    public Long getNumberOfClassTests() {
        return numberOfClassTests;
    }

    public void setNumberOfClassTests(Long numberOfClassTests) {
        this.numberOfClassTests = numberOfClassTests;
    }

    public double getTotalBillAmount() {
        return totalBillAmount;
    }

    public void setTotalBillAmount(double totalBillAmount) {
        this.totalBillAmount = totalBillAmount;
    }

    public ExamCommittee getCommittee() {
        return committee;
    }

    public void setCommittee(ExamCommittee committee) {
        this.committee = committee;
    }

    public User getBillUser() {
        return billUser;
    }

    public void setBillUser(User billUser) {
        this.billUser = billUser;
    }

    public double getBillRate() {
        return billRate;
    }

    public void setBillRate(double billRate) {
        this.billRate = billRate;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }
}
