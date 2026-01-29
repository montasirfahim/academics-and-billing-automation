package com.example.repository;

import com.example.entity.GratuityBill;
import com.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GratuityBillRepository extends JpaRepository<GratuityBill,Long> {
    @Query("SELECT SUM(b.totalBillAmount) FROM GratuityBill b WHERE b.billUser=:user")
    Double getTotalBillByUser(@Param("user") User user);

    @Query("SELECT SUM(gb.totalBillAmount) FROM GratuityBill gb")
    Double getTotalBillSoFar();

}
