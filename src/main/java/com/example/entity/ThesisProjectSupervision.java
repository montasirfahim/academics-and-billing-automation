package com.example.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ThesisProjectSupervision {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false, name = "supervision_id")
    private Long id;

    private String taskName;

    @JoinColumn(name = "course_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Course course;

    @JoinColumn(name = "committee_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private ExamCommittee examCommittee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "external_id")
    private User externalTeacher;

    @ElementCollection
    @CollectionTable(
            name = "thesis_supervision_internals",
            joinColumns = @JoinColumn(name = "supervision_id")
    )
    private List<Internal> internalTeachers = new ArrayList<>();

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Internal {
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "internal_id")
        private User internalTeacher;
        private Long groupCount;
        private Long studentCount;
    }

}
