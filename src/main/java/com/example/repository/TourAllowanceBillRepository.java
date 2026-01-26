package com.example.repository;

import com.example.entity.TourAllowanceBill;
import com.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TourAllowanceBillRepository extends JpaRepository<TourAllowanceBill, Long> {
    public void deleteByBillId(Long billId);

    public List<TourAllowanceBill> findByUserOrderByBillIdDesc(User user);

    @Query("SELECT SUM(t.totalBillAmount) FROM TourAllowanceBill t WHERE t.user = :user")
    Double sumTotalBillAmountByUser(@Param("user") User user);

    @Query("SELECT SUM(t.totalBillAmount) FROM TourAllowanceBill t")
    Double sumTotalBillAmount();

    TourAllowanceBill findByBillId(Long billId);

    boolean existsByUserAndDepartureTimeFromHisUniversity(User user, LocalDateTime departureTime);
}
