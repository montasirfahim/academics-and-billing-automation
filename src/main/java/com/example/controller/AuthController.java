package com.example.controller;

import com.example.entity.User;
import com.example.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "login_form";
    }

    @GetMapping("/ping")
    @ResponseBody
    public String ping(){
        return "ok";
    }

    @GetMapping("/reset-password-form")
    public String showResetForm(@RequestParam("email") String email, Model model) throws MessagingException, UnsupportedEncodingException {
        if(email == null || !userService.existByEmail(email)) {
            model.addAttribute("status", "Invalid email");
            model.addAttribute("error", "Invalid email or user not found!");
            return "error_page";
        }
        model.addAttribute("email", email);
        userService.generateAndSendOTP(userService.getUserByEmail(email), "Verification OTP", "password reset");
        return "forgot_password_form";
    }


}
