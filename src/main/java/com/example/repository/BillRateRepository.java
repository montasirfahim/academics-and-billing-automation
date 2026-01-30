package com.example.repository;

import com.example.entity.BillRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BillRateRepository extends JpaRepository<BillRate, Long> {

    @Query("SELECT b.rate FROM BillRate b WHERE b.task = :task")
    Double getRateByTask(@Param("task") String task);

    @Query("SELECT b.rate FROM BillRate b WHERE b.task = :task AND b.rateParameter = :rateParameter")
    Double getRateByTaskAndRateParameter(@Param("task") String task, @Param("rateParameter") String rateParameter);

    BillRate getBillRateById(Long id);
}
