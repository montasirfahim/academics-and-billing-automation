package com.example.controller;
import com.example.entity.BillRate;
import com.example.entity.TourAllowanceBill;
import com.example.service.*;
import jakarta.servlet.http.HttpSession;

import com.example.entity.User;
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
    @Autowired
    private CourseService courseService;
    @Autowired
    private SemesterService semesterService;
    @Autowired
    private ExamCommitteeService examCommitteeService;
    @Autowired
    private TourAllowanceBillService tourAllowanceBillService;
    @Autowired
    private BillRateService billRateService;
    @Autowired
    private GratuityBillService gratuityBillService;

    @GetMapping("/")
    public String landingPage(HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        String userBtn = "Manage Users";
        String courseBtn = "Manage Courses";
        if(!user.getRole().equals("admin") && !user.getRole().equals("co-admin")) {
            userBtn = "Contact Directory";
            courseBtn = "All Courses";
        }
        model.addAttribute("userBtn", userBtn);
        model.addAttribute("courseBtn", courseBtn);
        return "home";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if(session.getAttribute("user") == null) {
            return  "redirect:/login";
        }

        User sessionUser = (User) session.getAttribute("user");
        User user = User.createSafeCopy(sessionUser);

        model.addAttribute("user", user);
        model.addAttribute("owner", "self");


        if(user.getRole().equals("admin") || user.getRole().equals("co-admin")) {
            long totalSemesters = semesterService.countAllSemesters();
            model.addAttribute("totalSemesters", totalSemesters);

            long totalCommittees = examCommitteeService.getTotalCommitteesCount();
            long totalCompletedCommittees = examCommitteeService.getTotalCompletedCommitteesCount();
            long totalActiveCommittees = examCommitteeService.getTotalActiveCommitteesCount();
            model.addAttribute("totalCommittees", totalCommittees);
            model.addAttribute("totalCompletedCommittees", totalCompletedCommittees);
            model.addAttribute("totalActiveCommittees", totalActiveCommittees);

            long totalCourses = courseService.getTotalCoursesCount();
            long totalTheoryCourses = courseService.getTotalTheoryCoursesCount();
            model.addAttribute("totalCourses", totalCourses);
            model.addAttribute("totalTheoryCourses", totalTheoryCourses);

            double totalTADABill = tourAllowanceBillService.getTotalTaDaBillSoFar();
            double totalGratuityBill = gratuityBillService.getTotalBillSoFar();
            model.addAttribute("totalTADABill", totalTADABill);
            model.addAttribute("totalGratuityBill", totalGratuityBill);

            double totalBills = totalTADABill + totalGratuityBill;
            model.addAttribute("totalBills", totalBills);

            List<BillRate> billRates = billRateService.getAllBillRate();
            model.addAttribute("billRates", billRates);

            return "admin_panel";
        }

        long totalCourses = courseService.getTotalCoursesByUser(user);
        long theoryCourses = courseService.getTotalCoursesByUserAndType(user, "Theory");
        long labCourses = courseService.getTotalCoursesByUserAndType(user, "Lab");
        System.out.println("theory: " + theoryCourses + user.getName());
        long totalSemesters = semesterService.countAllSemesters();
        long totalCommitteeChairman = examCommitteeService.getTotalCommitteesAsChairman(user);
        long totalCommitteeInternalMember = examCommitteeService.getTotalCommitteesAsInternalMember(user);
        long totalCommitteeExternalMember = examCommitteeService.getTotalCommitteesAsExternalMember(user);
        long totalCommittees = totalCommitteeChairman + totalCommitteeInternalMember + totalCommitteeExternalMember;
        double totalBills = gratuityBillService.getTotalBillByUser(sessionUser) + tourAllowanceBillService.getTotalTaDaBillByUser(sessionUser);

        List<TourAllowanceBill> tadaBills = tourAllowanceBillService.getAllTourAllowanceBillByUser(user);

        model.addAttribute("totalCourses", totalCourses);
        model.addAttribute("theoryCourses", theoryCourses);
        model.addAttribute("labCourses", labCourses);
        model.addAttribute("totalSemesters", totalSemesters);
        model.addAttribute("totalCommittees", totalCommittees);
        model.addAttribute("committeeChairman", totalCommitteeChairman);
        model.addAttribute("committeeMember", totalCommitteeInternalMember + totalCommitteeExternalMember);
        model.addAttribute("totalBills", totalBills);
        model.addAttribute("tadaBills", tadaBills);

        return "admin_dashboard";
    }

    @GetMapping("/user/profile/{id}")
    public String userProfile(HttpSession session, @PathVariable Long id, Model model) {
        if(session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        User targetUser = User.createSafeCopy(userService.getUserById(id));
        User currentUser = (User) session.getAttribute("user");

        if(targetUser.getUserId().equals(currentUser.getUserId())) {
            if(targetUser.getRole().equals("admin") || targetUser.getRole().equals("co-admin")) {
                return "redirect:/dashboard";
            }

            model.addAttribute("owner", "self");
            model.addAttribute("user", targetUser);

            long totalCourses = courseService.getTotalCoursesByUser(targetUser);
            long theoryCourses = courseService.getTotalCoursesByUserAndType(targetUser, "Theory");
            long labCourses = courseService.getTotalCoursesByUserAndType(targetUser, "Lab");
            long totalSemesters = semesterService.countAllSemesters();
            long totalCommitteeChairman = examCommitteeService.getTotalCommitteesAsChairman(targetUser);
            long totalCommitteeInternalMember = examCommitteeService.getTotalCommitteesAsInternalMember(targetUser);
            long totalCommitteeExternalMember = examCommitteeService.getTotalCommitteesAsExternalMember(targetUser);
            long totalCommittees = totalCommitteeChairman + totalCommitteeInternalMember + totalCommitteeExternalMember;

            double totalBills = tourAllowanceBillService.getTotalTaDaBillByUser(targetUser) + gratuityBillService.getTotalBillByUser(targetUser);
            List<TourAllowanceBill> tadaBills = tourAllowanceBillService.getAllTourAllowanceBillByUser(targetUser);
            model.addAttribute("totalCourses", totalCourses);
            model.addAttribute("theoryCourses", theoryCourses);
            model.addAttribute("labCourses", labCourses);
            model.addAttribute("totalSemesters", totalSemesters);
            model.addAttribute("totalCommittees", totalCommittees);
            model.addAttribute("committeeChairman", totalCommitteeChairman);
            model.addAttribute("committeeMember", totalCommitteeInternalMember + totalCommitteeExternalMember);
            model.addAttribute("totalBills", totalBills);
            model.addAttribute("tadaBills", tadaBills);

            return "admin_dashboard";
        }
        else if(targetUser.getRole().equals("admin") || targetUser.getRole().equals("co-admin")) {
            model.addAttribute("status", "Access Denied");
            model.addAttribute("error", "You are not allowed to visit any administrator's profile.");
            return "error_page";
        }
        else if(currentUser.getRole().equals("admin") || currentUser.getRole().equals("co-admin")) {
            model.addAttribute("user", targetUser);
            model.addAttribute("owner", "admins");

            long totalCourses = courseService.getTotalCoursesByUser(targetUser);
            long theoryCourses = courseService.getTotalCoursesByUserAndType(targetUser, "Theory");
            long labCourses = courseService.getTotalCoursesByUserAndType(targetUser, "Lab");
            long totalSemesters = semesterService.countAllSemesters();
            long totalCommitteeChairman = examCommitteeService.getTotalCommitteesAsChairman(targetUser);
            long totalCommitteeInternalMember = examCommitteeService.getTotalCommitteesAsInternalMember(targetUser);
            long totalCommitteeExternalMember = examCommitteeService.getTotalCommitteesAsExternalMember(targetUser);
            long totalCommittees = totalCommitteeChairman + totalCommitteeInternalMember + totalCommitteeExternalMember;

            double totalBills = tourAllowanceBillService.getTotalTaDaBillByUser(targetUser) + gratuityBillService.getTotalBillByUser(targetUser);

            List<TourAllowanceBill> tadaBills = tourAllowanceBillService.getAllTourAllowanceBillByUser(targetUser);

            model.addAttribute("totalCourses", totalCourses);
            model.addAttribute("theoryCourses", theoryCourses);
            model.addAttribute("labCourses", labCourses);
            model.addAttribute("totalSemesters", totalSemesters);
            model.addAttribute("totalCommittees", totalCommittees);
            model.addAttribute("committeeChairman", totalCommitteeChairman);
            model.addAttribute("committeeMember", totalCommitteeInternalMember + totalCommitteeExternalMember);
            model.addAttribute("totalBills", totalBills);
            model.addAttribute("tadaBills", tadaBills);

            return "admin_dashboard";
        }
        else{
            model.addAttribute("status", "Access Denied");
            model.addAttribute("error", "You are not allowed to visit other's profile");
            return "error_page";
        }
    }

    @GetMapping("/all_users")
    public String viewAllUsers(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if(user == null) {
            return "redirect:/login";
        }
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("visitor", user.getRole());
        return "all_users";
    }

    @GetMapping("/users/new")
    public String viewNewUserForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if(user == null) {
            return "redirect:/login";
        }
        if(!user.getRole().equals("admin") && !user.getRole().equals("co-admin")) {
            model.addAttribute("status", "Access Denied");
            model.addAttribute("error", "You are not allowed to perform this action.");
            return "error_page";
        }
        model.addAttribute("user", new User());
        return "user_form";
    }

    @PostMapping("/users/new")
    public String saveNewUser(@ModelAttribute User user, HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("user");
        if(loggedInUser == null) {
            return "redirect:/login";
        }
        if(!loggedInUser.getRole().equals("admin") && !loggedInUser.getRole().equals("co-admin")) {
            model.addAttribute("status", "Access Denied");
            model.addAttribute("error", "You are not allowed to perform this action.");
            return "error_page";
        }
        if(userService.getUserByEmail(user.getEmail()) != null) {
            model.addAttribute("status", "Access Denied");
            model.addAttribute("error", "User with this email already exists.");
            return "error_page";
        }

        if(user.getDistanceFromMBSTU() == null){
            user.setDistanceFromMBSTU(0);
        }
       userService.saveUser(user);
       return "redirect:/all_users";
    }

    @DeleteMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id, HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("user");
        if(loggedInUser == null) {
            return "redirect:/login";
        }
        if(!loggedInUser.getRole().equals("admin") && !loggedInUser.getRole().equals("co-admin")) {
            model.addAttribute("status", "Access Denied");
            model.addAttribute("error", "You are not allowed to perform this action.");
            return "error_page";
        }

        userService.deleteUserById(id);
        return "redirect:/all_users";
    }

    @GetMapping("/user/edit-designation/{id}")
    public String editDesignationForm(@PathVariable("id") Long id, HttpSession session, Model model) {
        User targetUser = User.createSafeCopy( userService.getUserById(id));
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

    @PutMapping("/api/user/edit-designation/{userId}")
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

    @PutMapping("api/user/reset-password")
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

    @GetMapping("/user/edit-grade/{id}")
    public String editGradeForm(@PathVariable("id") Long id, HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("user");
        if(currentUser == null){
            return "redirect:/login";
        }
        User targetUser = userService.getUserById(id);
        if(targetUser == null){
            model.addAttribute("status", "Not Found");
            model.addAttribute("message", "User not found");
            return "error_page";
        }
        if(userService.isAdministrator(currentUser)) {
            model.addAttribute("user", targetUser);
            return "edit_grade_form";
        }

        model.addAttribute("status", "Access Denied");
        model.addAttribute("error", "Only chairman and officers of department can update salary grade. Please contact them.");
        return "error_page";
    }

    @PutMapping("/api/user/update/salary-grade/{userId}")
    @ResponseBody
    public ResponseEntity<Object> updateSalaryGrade(HttpSession session, @RequestBody Map<String, String> payload, @PathVariable Long userId) {
        User currentUser = (User) session.getAttribute("user");
        Map<Object, Object> map = new HashMap<>();
        if(currentUser == null){
            map.put("message", "Unauthorized: Please login first.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(map);
        }

        if(!userService.isAdministrator(currentUser)) {
            map.put("message", "Forbidden: You are not allowed to perform this action.\nPlease contact Registrar or Deputy Registrar of ICT, MBSTU.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(map);
        }

        if(userId == null){
            map.put("message", "Bad Request: Invalid User ID");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
        }

        if(userService.updateSalaryGrade(userId, payload.get("newSalaryGrade"))){
            map.put("message", "Salary grade updated successfully");
            return ResponseEntity.status(HttpStatus.OK).body(map);
        }
        else{
            map.put("message", "Bad Request: User not found or something went wrong");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
        }
    }

}
