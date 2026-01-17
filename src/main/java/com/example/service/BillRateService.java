package com.example.service;

import com.example.entity.BillRate;
import com.example.repository.BillRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BillRateService {
    @Autowired
    private BillRateRepository billRateRepository;

    public void saveNewBillRate(BillRate billRate) {
        billRateRepository.save(billRate);
    }

    public double getRateByTask(String task){
        return billRateRepository.getRateByTask(task);
    }

    public double getRateByTaskAndParameter(String task, String parameter){
        return billRateRepository.getRateByTaskAndRateParameter(task, parameter);
    }

    public Boolean updateRateById(Long id, double newRate, Long modifiedById){
        BillRate billRate = billRateRepository.getBillRateById(id);
        if(billRate == null || newRate <= 0 || modifiedById == null || modifiedById <= 0){
            return false;
        }
        billRate.setRate(newRate);
        billRate.setModifiedById(modifiedById);
        billRate.setLastModified(LocalDateTime.now());
        billRateRepository.save(billRate);
        return true;
    }

    public List<BillRate> getAllBillRate(){
        return billRateRepository.findAll();
    }

    public BillRate getBillRateById(Long id){
        return billRateRepository.getBillRateById(id);
    }
}
