package com.example.service;

import com.example.entity.TourAllowanceBill;
import com.example.entity.User;
import com.example.repository.TourAllowanceBillRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TourAllowanceBillService {

    @Autowired
    private TourAllowanceBillRepository tourAllowanceBillRepository;

    @Transactional
    public TourAllowanceBill saveTourAllowanceBill(TourAllowanceBill tourAllowanceBill) {
        User billUser = tourAllowanceBill.getUser();
        Integer distanceFromMBSTU = billUser.getDistanceFromMBSTU();

        tourAllowanceBill.setTotalTravelDistance((long) (distanceFromMBSTU + 10));
        tourAllowanceBill.setDailyAllowance((long)1500);
        Long perKmFareRate = (long)20;
        Long totalBill = tourAllowanceBill.getDailyAllowance()*tourAllowanceBill.getTotalDayCount() + perKmFareRate*tourAllowanceBill.getTotalTravelDistance();

        tourAllowanceBill.setPerKmFareRate(perKmFareRate);
        tourAllowanceBill.setTotalBillAmount(totalBill);


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
}
