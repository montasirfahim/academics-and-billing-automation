package com.example.controller;
import jakarta.servlet.http.HttpSession;

import com.example.entity.User;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

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
        if (session.getAttribute("user") == null) {
            return  "redirect:/login";
        }

        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        return "admin_dashboard";
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

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteUserById(id);
        return "redirect:/all_users";
    }

}
