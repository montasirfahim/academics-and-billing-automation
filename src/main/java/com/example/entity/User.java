package com.example.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String name;
    private String email;

    @JsonIgnore
    private String password;

    private String role;
    private String phone;
    private String university;
    private String department;
    private String designation;
    private String salaryGrade;
    private String gradingCategory;
    private  boolean isChairman = false;
    private String userType;
    private Integer distanceFromMBSTU;

    @JsonIgnore
    private Long loginOTP;
    @JsonIgnore
    private LocalDateTime otpExpiryTime;


    public User() {}

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long user_id) {
        this.userId = user_id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhone(){
        return phone;
    }
    public void setPhone(String phone){
        this.phone = phone;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getPassword() {
        return password;
    }
    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDesignation() {
        return designation;
    }
    public void setDesignation(String designation) {
        this.designation = designation;
    }
    public String getSalaryGrade() {
        return salaryGrade;
    }
    public void setSalaryGrade(String salaryGrade) {
        this.salaryGrade = salaryGrade;
    }
    public String getGradingCategory() {
        return gradingCategory;
    }
    public void setGradingCategory(String gradingCategory) {
        this.gradingCategory = gradingCategory;
    }
    public String getUniversity() {
        return university;
    }
    public void setUniversity(String university) {
        this.university = university;
    }

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    public boolean isChairman() {
        return isChairman;
    }
    public void setChairman(boolean value) {
        isChairman = value;
    }

    public void setUserType(String user_type) {
        this.userType = user_type;
    }
    public String getUserType() {
        return userType;
    }

    public Integer getDistanceFromMBSTU() {
        return distanceFromMBSTU;
    }

    public void setDistanceFromMBSTU(Integer distanceFromMBSTU) {
        this.distanceFromMBSTU = distanceFromMBSTU;
    }

    public Long getLoginOTP() {
        return loginOTP;
    }

    public void setLoginOTP(Long loginOTP) {
        this.loginOTP = loginOTP;
    }

    public LocalDateTime getOtpExpiryTime() {
        return otpExpiryTime;
    }

    public void setOtpExpiryTime(LocalDateTime otpExpiryTime) {
        this.otpExpiryTime = otpExpiryTime;
    }
}
