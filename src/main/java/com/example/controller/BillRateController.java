package com.example.controller;

import com.example.entity.BillRate;
import com.example.entity.User;
import com.example.service.BillRateService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
public class BillRateController {
    @Autowired
    private BillRateService billRateService;

    @GetMapping("/update-bill-rate/{id}")
    public String updateBillRateForm(@PathVariable Long id, Model model, HttpSession session) {
        User user = (User)session.getAttribute("user");
        if(user == null){
            return "redirect:/login";
        }
        if(user.getRole().equals("admin") || user.getRole().equals("co-admin")){
            BillRate billRate = billRateService.getBillRateById(id);
            if(billRate == null){
                model.addAttribute("status", "Not Found");
                model.addAttribute("error", "Invalid bill ID");
                return "error_page";
            }
            else{
                model.addAttribute("billRate", billRate);
                return "edit-bill_rate-form";
            }
        }
        model.addAttribute("status", "Access Denied");
        model.addAttribute("error", "You are not allowed to perform this action.");
        return "error_page";

    }

    @PutMapping("/api/update-bill-rate")
    @ResponseBody
    public ResponseEntity<Object> updateBillRate(@RequestBody Map<String, String> payload, HttpSession session){
        User user = (User)session.getAttribute("user");
        Map<Object,Object> map = new HashMap<>();
        if(user == null){
            map.put("message", "Unauthorized: Please login first");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(map);
        }
        if(user.getRole().equals("admin") || user.getRole().equals("co-admin")){
            try{
                Long id = Long.parseLong(payload.get("id"));
                double newRate = Double.parseDouble(payload.get("newRate"));
                if(newRate <= 0 || id == null){
                    map.put("message", "Bad Request: Invalid Bill ID or Rate");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
                }
                if(billRateService.updateRateById(id, newRate)){
                    map.put("message", "Bill Rate Updated");
                    return ResponseEntity.status(HttpStatus.OK).body(map);
                }
                else{
                    map.put("message", "Bad Request: Bill Rate Not Updated");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
                }
            }catch(Exception e){
                map.put("message","Error: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
            }
        }

        map.put("message", "Forbidden: You are not allowed to perform this action.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(map);
    }
}
