package com.example.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Semester {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long semesterId;

    private String customSemesterCode;

    private Integer semesterHeldYear;
    private String semesterHeldMonths;
    private Integer heldEndingYear;
    private String heldEndingMonth;

    private Integer semesterScheduledYear;
    private String semesterScheduledMonths;
    private Integer scheduledEndingYear;
    private String scheduledEndingMonth;

    private String semesterParity; //odd-1st semester or even-2nd semester

}
