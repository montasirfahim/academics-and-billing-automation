package com.example.controller;

import com.example.entity.TourAllowanceBill;
import com.example.entity.User;
import com.example.service.TourAllowanceBillService;
import com.example.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class TourAllowanceBillController {

    @Autowired
    private UserService userService;

    @Autowired
    private TourAllowanceBillService tourAllowanceBillService;

    @GetMapping("/ta-da/new")
    public String newTaDaBillForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        if(!user.getRole().equals("admin") && !user.getRole().equals("co-admin")) {
            model.addAttribute("status", "Access denied");
            model.addAttribute("error", "You are not allowed to perform this action.");
            return "error_page";
        }

        List<User> externals = userService.getExternals();
        model.addAttribute("taDaObj", new TourAllowanceBill());
        model.addAttribute("users", externals);

        return "ta-da_bill_form";
    }

    @PostMapping("/ta-da/new")
    public String createNewTaDaBill(@ModelAttribute TourAllowanceBill tourAllowanceBill, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        if(!user.getRole().equals("admin") && !user.getRole().equals("co-admin")) {
            model.addAttribute("status", "Access denied");
            model.addAttribute("error", "You are not allowed to perform this action.");
            return "error_page";
        }

       try{
           User billuser = User.createSafeCopy(tourAllowanceBill.getUser());
           tourAllowanceBill.setUser(billuser);
           TourAllowanceBill savedBill = tourAllowanceBillService.saveTourAllowanceBill(tourAllowanceBill);


           redirectAttributes.addFlashAttribute("billuser", billuser);
           redirectAttributes.addFlashAttribute("bill", savedBill);
           return "redirect:/bill-success";

       } catch (IllegalStateException e){
           model.addAttribute("status", "Forbidden");
           model.addAttribute("error", e.getMessage());
           return "error_page";
       } catch (Exception e){
           model.addAttribute("status", "Error");
           model.addAttribute("error", e.getMessage());
           return "error_page";
       }
    }

    @GetMapping("/bill-success")
    public String showSuccessPage() {
        return "ta-da_bill_success";
    }

    @GetMapping("/show-error")
    public String showErrorPage() {
        return "error_page";
    }
}
