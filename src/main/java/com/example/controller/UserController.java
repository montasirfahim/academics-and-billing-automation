package com.example.controller;
import jakarta.servlet.http.HttpSession;

import com.example.entity.User;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/")
    public String landingPage(HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        return "home";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if(session.getAttribute("user") == null) {
            return  "redirect:/login";
        }

        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        model.addAttribute("owner", "self");
        return "admin_dashboard";
    }

    @GetMapping("/user/profile/{id}")
    public String userProfile(HttpSession session, @PathVariable Long id, Model model) {
        if(session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        User targetUser = userService.getUserById(id);
        User currentUser = (User) session.getAttribute("user");
        if(targetUser.getUserId().equals(currentUser.getUserId())) {
            model.addAttribute("owner", "self");
            model.addAttribute("user", targetUser);
            return "admin_dashboard";
        }
        else if(currentUser.getRole().equals("admin") || currentUser.getRole().equals("co-admin")) {
            model.addAttribute("user", targetUser);
            model.addAttribute("owner", "admins");
            return "admin_dashboard";
        }
        else{
            model.addAttribute("status", "Access Denied");
            model.addAttribute("error", "You are not allowed to visit this profile");
            return "error_page";
        }
    }

    @GetMapping("/all_users")
    public String viewAllUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "all_users";
    }
    @GetMapping("/users/new")
    public String viewNewUserForm(Model model) {
        model.addAttribute("user", new User());
        return "user_form";
    }
    @PostMapping("/users/new")
    public String saveNewUser(@ModelAttribute User user) {
        if(user.getDistanceFromMBSTU() == null){
            user.setDistanceFromMBSTU(0);
        }
       userService.saveUser(user);
       return "redirect:/all_users";
    }

    @GetMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteUserById(id);
        return "redirect:/all_users";
    }

    @GetMapping("/user/edit-designation/{id}")
    public String editDesignation(@PathVariable("id") Long id, HttpSession session, Model model) {
        User targetUser = userService.getUserById(id);
        if(targetUser == null){
            return "redirect:/home";
        }
        User currentUser = (User) session.getAttribute("user");
        if(currentUser.getRole().equals("admin") || currentUser.getRole().equals("co-admin")) {
            model.addAttribute("user", targetUser);
            return "edit_designation_form";
        }

        model.addAttribute("status", "Access Denied");
        model.addAttribute("error", "Only chairman and officers of department can update designation. Please contact them.");
        return "error_page";
    }

    @PostMapping("/api/user/edit-designation/{userId}")
    @ResponseBody
    public ResponseEntity<Object> editDesignation(@PathVariable("userId") Long targetUserId, HttpSession session, @RequestBody Map<String, String> payload) {
        User targetUser = userService.getUserById(targetUserId);
        User currentUser = (User) session.getAttribute("user");
        Map<Object, Object> map = new HashMap<>();
        if(targetUser == null || currentUser == null){
            map.put("status", "Access Denied");
            map.put("message", "User not found");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
        }

        if(payload.get("newDesignation") != null && (currentUser.getRole().equals("admin") || currentUser.getRole().equals("co-admin"))) {
            if(userService.updateDesignation(targetUserId, payload.get("newDesignation"))){
                return ResponseEntity.ok(map);
            }
            else{
                map.put("message", "User not found");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
            }
        }

        map.put("status", "Access Denied");
        map.put("message", "Only chairman and officers of department can update designation. Please contact them.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(map);
    }

    @GetMapping("user/reset-password")
    public String resetPasswordForm(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if(user == null){
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "pass_reset_form";
    }

    @PostMapping("api/user/reset-password")
    @ResponseBody
    public ResponseEntity<Object> resetPassword(HttpSession session, @RequestBody Map<String, String> payload) {
        User currentUser = (User) session.getAttribute("user");
        Map<Object, Object> map = new HashMap<>();
        if(currentUser == null){
            map.put("status", "Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(map);
        }
        String userIdString = payload.get("userId");
        if(userIdString == null){
            map.put("status", "Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(map);
        }

        Long id = Long.parseLong(userIdString);
        String currentPassword = payload.get("currentPassword");
        String newPassword = payload.get("newPassword");

        if(currentPassword == null || newPassword == null){
            map.put("status", "Error");
            map.put("message", "Invalid User Id or Password");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
        }
        if(currentUser.getUserId().equals(id)){
            if(userService.checkPassword(currentUser.getUserId(),  currentPassword)){
                if(!userService.updatePassword(currentUser.getUserId(), newPassword)){
                    map.put("status", "Error");
                    map.put("message", "User no longer exists");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
                }
                map.put("status", "Success");
                map.put("message", "Password Reset Successful");
                return ResponseEntity.status(HttpStatus.OK).body(map);
            }
            else{
                map.put("status", "Error");
                map.put("message", "Incorrect current password");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
            }
        }

        map.put("status", "Error");
        map.put("message", "Something went wrong");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
    }
}
