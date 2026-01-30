package com.example.controller;

import com.example.entity.User;
import com.example.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    @Autowired
    private UserService userService;


    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> payload,
                                                     HttpSession session) throws MessagingException, UnsupportedEncodingException {
        String email = payload.get("email");
        String password = payload.get("password");

        User user = userService.validateUser(email, password);


        Map<String, Object> response = new HashMap<>();
        if(user != null) {
            //userService.generateAndSendOTP(user, "Login Verification OTP", "login"); //will enable later
            session.setAttribute("userId", user.getUserId()); //safer
            session.setAttribute("user", user); //will be deleted later
            response.put("status", "success");
            response.put("message", "A OTP has been sent to your email. Please verify withing 3 min.");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            response.put("message", "Incorrect username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, Object>> verifyOtp(@RequestBody Map<String, String> payload, HttpSession session, HttpServletRequest request) {
        String otp = payload.get("otp");
        Map<String, Object> response = new HashMap<>();

        if(session == null || session.getAttribute("userId") == null) {
            response.put("status", "error");
            response.put("message", "Unauthorized User");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Long userId = (Long) session.getAttribute("userId");
        User user = userService.getUserById(userId);

        if(user == null) {
            response.put("status", "error");
            response.put("message", "Unauthorized User");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        if(user.getOtpExpiryTime().isBefore(LocalDateTime.now())) {
            response.put("status", "error");
            response.put("message", "OTP time expired!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        if(user.getLoginOTP().equals(otp) && user.getOtpExpiryTime().isAfter((LocalDateTime.now()))) {
            session.invalidate();
            user.setLoginOTP(null);
            user.setOtpExpiryTime(null);
            userService.updateUser(user);

            HttpSession newSession = request.getSession(true);
            newSession.setAttribute("user", user);
            response.put("status", "success");
            response.put("message", "OTP verified successfully");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }

        response.put("status", "error");
        response.put("message", "Incorrect OTP");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Logged out successfully"
        ));
    }

    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        boolean exists = userService.existByEmail(email);
        return ResponseEntity.ok(exists);
    }

    @PostMapping("/reset-forgotten-password")
    public ResponseEntity<Map<String, Object>> resetForgottenPassword(@RequestBody Map<String, String> payload, HttpSession session) {
        Map<String, Object> map = new HashMap<>();

        String email = payload.get("email");
        String newPassword = payload.get("newPassword");
        String confirmPassword = payload.get("confirmedPassword");
        String otp = payload.get("otp");

        if(email == null || newPassword == null || confirmPassword == null || otp == null) {
            map.put("status", "error");
            map.put("message", "Invalid Parameters: Please check your email, password or OTP.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
        }
        if(!userService.existByEmail(email)) {
            map.put("status", "error");
            map.put("message", "Bad Request: Email not found");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
        }

        User user = userService.getUserByEmail(email);

        if(user.getLoginOTP() == null || !user.getLoginOTP().equals(otp)){
            map.put("status", "error");
            map.put("message", "Bad Request: Incorrect OTP");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
        }
        if(user.getOtpExpiryTime().isBefore(LocalDateTime.now())) {
            map.put("status", "error");
            map.put("message", "Bad Request: OTP time expired!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
        }

        if(newPassword.equals(confirmPassword) && newPassword.length() >= 6) {
            if(userService.updatePassword(user.getUserId(), newPassword)) {
                session.invalidate();
                map.put("status", "success");
                map.put("message", "Your password has been reset successfully");
                return ResponseEntity.status(HttpStatus.OK).body(map);

            }else{
                map.put("status", "error");
                map.put("message", "Oops! Internal Error... Please try again.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
            }
        }

        map.put("status", "error");
        map.put("message", "Bad Request: Something went wrong. Please try again carefully.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
    }

}
