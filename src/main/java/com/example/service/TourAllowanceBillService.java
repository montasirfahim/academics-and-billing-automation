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


    @Transactional
    public TourAllowanceBill saveTourAllowanceBill(TourAllowanceBill tourAllowanceBill) {
        User billUser = tourAllowanceBill.getUser();
        Integer distanceFromMBSTU = billUser.getDistanceFromMBSTU();

        Map<String, Double> map = new HashMap<>();
        map.put("Grade 1", 1400.00);
        map.put("Grade 2", 1225.00);
        map.put("Grade 3", 1225.00);
        map.put("Grade 4", 1050.00);
        map.put("Grade 5", 1050.00);
        map.put("Grade 6", 900.00);
        map.put("Grade 7", 900.00);
        map.put("Grade 8", 875.00);
        map.put("Grade 9", 875.00);
        map.put("Grade 10", 875.00);

        double dailyAllowance = map.get(billUser.getSalaryGrade());

        tourAllowanceBill.setTotalTravelDistance(2*(distanceFromMBSTU + 10));
        tourAllowanceBill.setDailyAllowance(dailyAllowance);

        double perKmFareRate = 18.0;
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
