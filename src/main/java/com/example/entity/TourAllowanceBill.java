package com.example.entity;

import jakarta.persistence.*;

import java.sql.Time;

@Entity
public class TourAllowanceBill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long billId;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private Time departureTimeFromHisUniversity;
    private Time arrivalTimeAtTangail1;
    private Time arrivalTimeAtMbstu;

    private Time departureTimeFromMbstu;
    private Time arrivalTimeAtTangail2;
    private Time arrivalTimeAtHisUniversity;

    private Long dailyAllowance;
    private Long totalDayCount;

    private Long perKmFareRate;
    private Long totalTravelDistance;

    private Long totalBillAmount;

    public TourAllowanceBill() {}



}
