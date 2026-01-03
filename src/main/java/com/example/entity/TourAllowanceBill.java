package com.example.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class TourAllowanceBill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long billId;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    private String travelIntention;

    private LocalDateTime departureTimeFromHisUniversity;
    private LocalDateTime arrivalTimeAtTangail1;
    private LocalDateTime arrivalTimeAtMbstu;
    private LocalDateTime departureTimeFromMbstu;
    private LocalDateTime arrivalTimeAtTangail2;
    private LocalDateTime arrivalTimeAtHisUniversity;

    private double dailyAllowance;
    private Long totalDayCount;
    private double perKmFareRate;
    private double totalTravelDistance;
    private double totalBillAmount;

    private String transportationType;

    public TourAllowanceBill() {}

    // Getters and Setters
    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getDepartureTimeFromHisUniversity() {
        return departureTimeFromHisUniversity;
    }

    public void setDepartureTimeFromHisUniversity(LocalDateTime departureTimeFromHisUniversity) {
        this.departureTimeFromHisUniversity = departureTimeFromHisUniversity;
    }

    public LocalDateTime getArrivalTimeAtTangail1() {
        return arrivalTimeAtTangail1;
    }

    public void setArrivalTimeAtTangail1(LocalDateTime arrivalTimeAtTangail1) {
        this.arrivalTimeAtTangail1 = arrivalTimeAtTangail1;
    }

    public LocalDateTime getArrivalTimeAtMbstu() {
        return arrivalTimeAtMbstu;
    }

    public void setArrivalTimeAtMbstu(LocalDateTime arrivalTimeAtMbstu) {
        this.arrivalTimeAtMbstu = arrivalTimeAtMbstu;
    }

    public LocalDateTime getDepartureTimeFromMbstu() {
        return departureTimeFromMbstu;
    }

    public void setDepartureTimeFromMbstu(LocalDateTime departureTimeFromMbstu) {
        this.departureTimeFromMbstu = departureTimeFromMbstu;
    }

    public LocalDateTime getArrivalTimeAtTangail2() {
        return arrivalTimeAtTangail2;
    }

    public void setArrivalTimeAtTangail2(LocalDateTime arrivalTimeAtTangail2) {
        this.arrivalTimeAtTangail2 = arrivalTimeAtTangail2;
    }

    public LocalDateTime getArrivalTimeAtHisUniversity() {
        return arrivalTimeAtHisUniversity;
    }

    public void setArrivalTimeAtHisUniversity(LocalDateTime arrivalTimeAtHisUniversity) {
        this.arrivalTimeAtHisUniversity = arrivalTimeAtHisUniversity;
    }

    public double getDailyAllowance() {
        return dailyAllowance;
    }

    public void setDailyAllowance(double dailyAllowance) {
        this.dailyAllowance = dailyAllowance;
    }

    public Long getTotalDayCount() {
        return totalDayCount;
    }

    public void setTotalDayCount(Long totalDayCount) {
        this.totalDayCount = totalDayCount;
    }

    public double getPerKmFareRate() {
        return perKmFareRate;
    }

    public void setPerKmFareRate(double perKmFareRate) {
        this.perKmFareRate = perKmFareRate;
    }

    public double getTotalTravelDistance() {
        return totalTravelDistance;
    }

    public void setTotalTravelDistance(double totalTravelDistance) {
        this.totalTravelDistance = totalTravelDistance;
    }

    public double getTotalBillAmount() {
        return totalBillAmount;
    }

    public void setTotalBillAmount(double totalBillAmount) {
        this.totalBillAmount = totalBillAmount;
    }

    public String getTravelIntention() {
        return travelIntention;
    }

    public void setTravelIntention(String travelIntention) {
        this.travelIntention = travelIntention;
    }

    public String getTransportationType() {
        return transportationType;
    }

    public void setTransportationType(String transportationType) {
        this.transportationType = transportationType;
    }
}