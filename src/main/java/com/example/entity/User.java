package com.example.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String name;
    private String email;

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
    private String password;
    @JsonIgnore
    private String loginOTP;
    @JsonIgnore
    private LocalDateTime otpExpiryTime;
    @JsonIgnore
    private Boolean isOTPVerified = false;


    public static User createSafeCopy(User other) {
        if (other == null) return null;

        User safeUser = new User();
        safeUser.setUserId(other.getUserId());
        safeUser.setName(other.getName());
        safeUser.setEmail(other.getEmail());
        safeUser.setRole(other.getRole());
        safeUser.setPhone(other.getPhone());
        safeUser.setUniversity(other.getUniversity());
        safeUser.setDepartment(other.getDepartment());
        safeUser.setDesignation(other.getDesignation());
        safeUser.setSalaryGrade(other.getSalaryGrade());
        safeUser.setGradingCategory(other.getGradingCategory());
        safeUser.setChairman(other.isChairman());
        safeUser.setUserType(other.getUserType());
        safeUser.setDistanceFromMBSTU(other.getDistanceFromMBSTU());

        return safeUser;
    }
}
