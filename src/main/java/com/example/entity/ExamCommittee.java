package com.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class ExamCommittee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long committeeId;

    private String session;
    private String semesterYearName;
    private Long studentCount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    //many committees have the same user
    @ManyToOne
    @JoinColumn(name = "chairman_id")
    private User chairman;

    @ManyToOne
    @JoinColumn(name = "internal_member1_id")
    private User internalMember1;

    @ManyToOne
    @JoinColumn(name = "internal_member2_id")
    private User internalMember2;

    @ManyToOne
    @JoinColumn(name = "external_member1_id")
    private User externalMember1;


    private String moderationCallDateTime;
    private String moderationScheduledDateTime;

    private boolean isModerated = false;
    private boolean isResultPublished = false;
    private boolean isCompleted = false;


    private boolean quesPrintingStatus = false;
    private Long quesCountOfChairman;
    private Long quesCountOfMember1;
    private Long quesCountOfMember2;


    public ExamCommittee(Semester semester, String session, User chairman, User internal_member1, User internal_member2, User external_member1) {
        this.semester = semester;
        this.session = session;
        this.chairman = chairman;
        this.internalMember1 = internal_member1;
        this.internalMember2 = internal_member2;
        this.externalMember1 = external_member1;
    }


    public boolean getIsCompleted() {
        return isCompleted;
    }
    public void setIsCompleted(boolean completed) {
        isCompleted = completed;
    }

}