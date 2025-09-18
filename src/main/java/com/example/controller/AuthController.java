package com.example.controller;

import com.example.entity.User;
import com.example.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "login_form";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String email,
                          @RequestParam String password,
                          HttpSession session, Model model) {
        System.out.println("Trying login with email=" + email + " password=" + password);
        User user = userService.validateUser(email, password);

        if (user != null) {
            session.setAttribute("user", user);
            System.out.println("Login success for user: " + user.getEmail());
            return "redirect:/home";
        } else {
            model.addAttribute("error", "Incorrect username or password");
            return "login_form";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
