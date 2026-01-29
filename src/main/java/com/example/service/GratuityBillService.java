package com.example.service;

import com.example.entity.GratuityBill;
import com.example.entity.User;
import com.example.repository.GratuityBillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GratuityBillService {
    @Autowired
    private GratuityBillRepository gratuityBillRepository;

    public void saveGratuityBill(GratuityBill gratuityBill){
        gratuityBillRepository.save(gratuityBill);
    }

    public double getTotalBillByUser(User user){
        Double totalBillByUser = gratuityBillRepository.getTotalBillByUser(user);
        return (totalBillByUser != null) ? totalBillByUser : 0.0;
    }

    public double getTotalBillSoFar(){
        Double totalBillSoFar = gratuityBillRepository.getTotalBillSoFar();
        return (totalBillSoFar != null) ? totalBillSoFar : 0.0;
    }
}
