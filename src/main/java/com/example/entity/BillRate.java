package com.example.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class BillRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String task;
    private double rate;
    private String rateParameter;

    private LocalDateTime lastModified;

    private Long modifiedById;

    public BillRate() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getRateParameter() {
        return rateParameter;
    }

    public void setRateParameter(String rateParameter) {
        this.rateParameter = rateParameter;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }


    public Long getModifiedById() {
        return modifiedById;
    }

    public void setModifiedById(Long modifiedById) {
        this.modifiedById = modifiedById;
    }
}
