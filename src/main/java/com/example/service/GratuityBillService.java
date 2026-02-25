package com.example.service;

import com.example.entity.ExamCommittee;
import com.example.entity.GratuityBill;
import com.example.entity.User;
import com.example.repository.GratuityBillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public double getTotalBillAmountByUserAndExamCommittee(User user, ExamCommittee examCommittee){
        Double total = gratuityBillRepository.getTotalBillByUserAndExamCommittee(user, examCommittee);
        return (total != null) ? total : 0.0;
    }

    public List<GratuityBill> findAllByUserAndExamCommittee(User user, ExamCommittee examCommittee){
        return  gratuityBillRepository.findAllByBillUserAndExamCommittee(user, examCommittee);
    }

    public double getTotalBillAmountByExamCommittee(ExamCommittee examCommittee){
        Double total = gratuityBillRepository.getTotalBillByExamCommittee(examCommittee);
        return (total != null) ? total : 0.0;
    }
}
