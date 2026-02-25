package com.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    String courseCode;
    String session;
    private String courseName;
    private double courseCredit;
    private String courseType;
    private String courseStatus;
    private Long examineeCount = 0L;

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


}

