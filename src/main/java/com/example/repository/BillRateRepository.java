package com.example.repository;

import com.example.entity.BillRate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillRateRepository extends JpaRepository<BillRate, Long> {

    double getRateByTask(String task);
    double getRateByTaskAndRateParameter(String task, String rateParameter);
    BillRate getBillRateById(Long id);
}
