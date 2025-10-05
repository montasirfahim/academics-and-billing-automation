package com.example.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class ExamCommittee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long committeeId;

    private String session;

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

    private boolean isModerated = false;
    private String moderationCallDateTime;
    private String moderationScheduledDateTime;
    private boolean isCompleted = false;
    private String semesterYearName;

    public ExamCommittee() {}

    public ExamCommittee(Semester semester, String session, User chairman, User internal_member1, User internal_member2, User external_member1) {
        this.semester = semester;
        this.session = session;
        this.chairman = chairman;
        this.internalMember1 = internal_member1;
        this.internalMember2 = internal_member2;
        this.externalMember1 = external_member1;
    }

    public Long getCommitteeId() {
        return committeeId;
    }

    public void setCommitteeId(Long committeeId) {
        this.committeeId = committeeId;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public User getChairman() {
        return chairman;
    }

    public void setChairman(User chairman) {
        this.chairman = chairman;
    }

    public User getInternalMember1() {
        return internalMember1;
    }
    public void setInternalMember1(User internalMember1) {
        this.internalMember1 = internalMember1;
    }
    public User getInternalMember2() {
        return internalMember2;
    }
    public void setInternalMember2(User internalMember2) {
        this.internalMember2 = internalMember2;
    }
    public User getExternalMember1() {
        return externalMember1;
    }
    public void setExternalMember1(User externalMember1) {
        this.externalMember1 = externalMember1;
    }
    public Semester getSemester() {
        return semester;
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
    }

    public boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(boolean completed) {
        isCompleted = completed;
    }



    public String getSemesterYearName() {
        return semesterYearName;
    }

    public void setSemesterYearName(String semesterYearName) {
        this.semesterYearName = semesterYearName;
    }

    public boolean isModerated() {
        return isModerated;
    }

    public void setModerated(boolean moderated) {
        isModerated = moderated;
    }

    public String getModerationCallDateTime() {
        return moderationCallDateTime;
    }

    public void setModerationCallDateTime(String moderationDateTime) {
        this.moderationCallDateTime = moderationDateTime;
    }

    public String getModerationScheduledDateTime() {
        return moderationScheduledDateTime;
    }

    public void setModerationScheduledDateTime(String moderationScheduledDateTime) {
        this.moderationScheduledDateTime = moderationScheduledDateTime;
    }
}