package com.example.service;

import com.example.entity.TourAllowanceBill;
import com.example.entity.User;
import com.example.repository.TourAllowanceBillRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TourAllowanceBillService {

    @Autowired
    private TourAllowanceBillRepository tourAllowanceBillRepository;
    @Autowired
    private BillRateService billRateService;


    @Transactional
    public TourAllowanceBill saveTourAllowanceBill(TourAllowanceBill tourAllowanceBill) {
        User billUser = tourAllowanceBill.getUser();
        Integer distanceFromMBSTU = billUser.getDistanceFromMBSTU();

        double dailyAllowance = billRateService.getRateByTaskAndParameter("Daily Allowance", billUser.getSalaryGrade());

        tourAllowanceBill.setTotalTravelDistance(2*(distanceFromMBSTU + 10));
        tourAllowanceBill.setDailyAllowance(dailyAllowance);

        double perKmFareRate = billRateService.getRateByTask("Travelling");

        double totalBill = dailyAllowance*tourAllowanceBill.getTotalDayCount() + perKmFareRate*tourAllowanceBill.getTotalTravelDistance();

        tourAllowanceBill.setPerKmFareRate(perKmFareRate);
        tourAllowanceBill.setTotalBillAmount(totalBill);
        tourAllowanceBill.setTransportationType("Public Transportation");

        return tourAllowanceBillRepository.save(tourAllowanceBill);
    }

    public Double getTotalTaDaBillByUser(User user) {
        Double total = tourAllowanceBillRepository.sumTotalBillAmountByUser(user);
        return (total != null) ? total : 0.0;
    }

    public Double getTotalTaDaBillSoFar(){
        Double total = tourAllowanceBillRepository.sumTotalBillAmount();
        return (total != null) ? total : 0.0;
    }

    public List<TourAllowanceBill> getAllTourAllowanceBillByUser(User user) {
        return tourAllowanceBillRepository.findByUserOrderByBillIdDesc(user);
    }

    public TourAllowanceBill findById(Long tadaBillId) {
        return tourAllowanceBillRepository.findByBillId(tadaBillId);
    }
}
