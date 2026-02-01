package com.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommitteeActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "performed_by")
    private User performedBy;

    @ManyToOne
    @JoinColumn(name="committee_id")
    private ExamCommittee examCommittee;

    private String actionTitle;
    private LocalDate timestamp;
    private String details;
    private Integer priority = 0;

    public CommitteeActivity(CommitteeActivity other) {
        this.performedBy = other.performedBy;
        this.examCommittee = other.examCommittee;
        this.timestamp = other.timestamp;
        this.priority = other.priority;
        this.actionTitle = other.actionTitle;
        this.details = other.details;
    }

}
