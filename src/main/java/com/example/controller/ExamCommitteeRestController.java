package com.example.controller;

import com.example.entity.CommitteeActivity;
import com.example.entity.Course;
import com.example.entity.ExamCommittee;
import com.example.entity.User;
import com.example.service.*;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/committee")
public class ExamCommitteeRestController {
    @Autowired
    private ExamCommitteeService examCommitteeService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UtilityService utilityService;
    @Autowired
    private UserService userService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private BrevoEmailService brevoEmailService;
    @Autowired
    private CommitteeActivityService committeeActivityService;

    @GetMapping("/api/{id}")
    public ResponseEntity<ExamCommittee> getCommittee(@PathVariable Long id) {
        ExamCommittee committee = examCommitteeService.findCommitteeByCommitteeId(id);
        if (committee == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Return 404 if not found
        }

        HttpHeaders headers = new HttpHeaders();
        System.out.println(committee.getChairman().getName());
        headers.add("Content-Type", "application/json");

        return new ResponseEntity<>(committee, headers, HttpStatus.OK);
    }

    @PutMapping("/api/updateStatus/{id}")
    public ResponseEntity<ExamCommittee> updateCommitteeStatus(@PathVariable Long id, @RequestBody Map<String, Boolean> statusUpdate, HttpSession session) {
        Boolean isComplete = statusUpdate.get("isComplete"); //target status
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if(isComplete == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        ExamCommittee committee = examCommitteeService.findCommitteeByCommitteeId(id);
        if (committee == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if((!committee.isModerated() || !committee.isResultPublished()) && isComplete){//guard only when user wants to make Completed
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        if(examCommitteeService.checkEditPermission(user, committee)) {
            try {
                ExamCommittee updatedCommittee = examCommitteeService.updateStatus(id, isComplete);
                return new ResponseEntity<>(updatedCommittee, HttpStatus.OK); // 200 OK
            } catch (RuntimeException e) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
            }
        }
        else{
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

    }

    @PutMapping("api/moderation/{id}")
    public ResponseEntity<String> callModerationMeeting(@PathVariable Long id, @RequestBody Map<String, String> payload, HttpSession session) throws MessagingException, UnsupportedEncodingException {
        User user =(User) session.getAttribute("user");
        if(user == null) {
            return new ResponseEntity<>("Unauthorized: Please login first.", HttpStatus.UNAUTHORIZED);
        }
        ExamCommittee committee = examCommitteeService.findCommitteeByCommitteeId(id);
        if(!user.getRole().equals("admin") && !user.getRole().equals("co-admin") && !(user.getUserId().equals(committee.getChairman().getUserId())) ) {
            return  new ResponseEntity<>("Forbidden: You are not allowed to call this meeting", HttpStatus.FORBIDDEN);
        }
        if(committee == null) {
            return new ResponseEntity<>("Committee not found.", HttpStatus.NOT_FOUND);
        }

        if(committee.isModerated()){
            return  new ResponseEntity<>("Questions Already Moderated!", HttpStatus.CONFLICT);
        }

       if(!examCommitteeService.checkQuesModerationEligibility(committee)) {
           return  new ResponseEntity<>("This committee is not ready yet to call moderation meeting. Please assign course teacher, question setter and script evaluator for all courses and check everything!", HttpStatus.BAD_REQUEST);
       }

        String meetingDateTime = payload.get("meetingTime");
        String callTime = payload.get("callTime");
        Instant instant = Instant.parse(payload.get("dateObj"));

        LocalDateTime meetingTimeObj = LocalDateTime.ofInstant(instant, ZoneId.of("Asia/Dhaka"));
        String googleCalenderURL = utilityService.calendarLink(meetingTimeObj);

        //more business logic
        String htmlBody = """
        <div style="font-family: 'Segoe UI', Arial, sans-serif; color: #333; line-height: 1.6;">
            <p>Dear Sir,</p> 
            <p>
                I hope this message finds you well. This is to inform you that a 
                <b>Question Moderation Meeting</b> has been scheduled as part of the upcoming semester final examination.
            </p>
    
            <p>
                <b>Meeting Date & Time:</b> <span style="color: #1a73e8;">""" + meetingDateTime + """ 
            </span>
            </p>
                <div style="margin: 20px 0;">
                        <a href=""" + googleCalenderURL + """ 
                            target="_blank" style="
                            display: inline-block;
                            padding: 10px 20px;
                            background-color: #4285F4;
                            color: #ffffff;
                            text-decoration: none;
                            font-weight: bold;
                            border-radius: 5px;
                            box-shadow: 0 2px 4px rgba(0,0,0,0.2);
                            font-size: 14px;
                        ">
                            Save Event to Google Calendar
                        </a>
                    </div>
    
            <hr style="border: none; border-top: 1px solid #ddd; margin: 16px 0;">
    
            <h3 style="color: #444; margin-bottom: 4px;">Committee Overview</h3>
            <p style="margin: 0;">
                <b>Exam:</b>""" + " " + committee.getSemesterYearName() + " " + committee.getSemester().getSemesterParity() +
                    " Semester Final Examination - " + committee.getSemester().getSemesterScheduledYear() + """
            </p>
           
            <p style="margin: 0;">
               <b>Session:</b>""" + " " + committee.getSession() + """
            </p>
           
            <p style="margin: 0;">
                <b>Committee Chairman:</b>""" + " " + committee.getChairman().getName() + """
            </p>
    
            <hr style="border: none; border-top: 1px solid #ddd; margin: 16px 0;">
    
            <p>
                Kindly make the necessary preparations and ensure your availability for the meeting.  
                Your presence and contributions will be highly appreciated.
            </p>
    
            <br>
            <p>Best regards,</p>
            <p>
                <b>""" + committee.getChairman().getName() + """ 
                </b><br> Committee Chairman <br>
                Department of Information and Communication Technology <br>
                Mawlana Bhashani Science and Technology University<br>
                Tangail - 1902, Bangladesh
            </p>
        </div>
        """;


        brevoEmailService.sendEmail(
                new String[] {"montasirtuhin1128@gmail.com", "it22016@mbstu.ac.bd"},
                "Question Moderation Meeting",
                htmlBody,
                null
        );

        System.out.println("Email sent, chairman: " + committee.getChairman().getName());
        committee.setModerationCallDateTime(callTime);
        committee.setModerationScheduledDateTime(meetingDateTime);
        committee.setModerated(true);
        examCommitteeService.saveCommittee(committee);

        CommitteeActivity activity = new CommitteeActivity();
        activity.setPerformedBy(user);
        activity.setExamCommittee(committee);
        activity.setTimestamp(LocalDate.now());
        activity.setPriority(5);
        activity.setActionTitle("Question Moderation Meeting Call");
        activity.setDetails("Question Moderation Meeting of this exam committee has been scheduled for " + meetingDateTime);
        committeeActivityService.saveCommitteeActivity(activity);

        return new ResponseEntity<>("Moderation meeting has been called successfully!", HttpStatus.OK);

    }

    @PutMapping("/api/assign-setter")
    public ResponseEntity<Object> assignQuesSetterAndEvaluator(HttpSession session, @RequestBody Map<String, String> payload) {
        User user = (User) session.getAttribute("user");
        Map<Object, Object> map = new HashMap<>();
        if (user == null) {
            map.put("status", "Unauthorized");
            map.put("message", "Unauthorized: You are not logged in!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(map);
        }

        try {
            Long committeeId = Long.parseLong(payload.get("committeeId"));
            Long courseId = Long.parseLong(payload.get("courseId"));
            Long internalTeacherId = Long.parseLong(payload.get("internalTeacherId"));
            Long externalTeacherId = Long.parseLong(payload.get("externalTeacherId"));

            ExamCommittee committee = examCommitteeService.findCommitteeByCommitteeId(committeeId);
            User internalTeacher = userService.getUserById(internalTeacherId);
            User externalTeacher = userService.getUserById(externalTeacherId);
            User loggedInUser = (User) session.getAttribute("user");

            if(committee == null || internalTeacher == null || externalTeacher == null || loggedInUser == null) {
                map.put("message", "Not Found All Required Data");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
            }

            Course course = courseService.findById(courseId);
            if(course == null) {
                map.put("message", "Course Not Found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
            }
            if(committee.isResultPublished()){
                map.put("message", "Conflict: Result of this exam committee has already published!\nYou can't re-assign question setter and examiner right now.");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(map);
            }
            if(examCommitteeService.isNotCommitteeCourse(committee, course)) {
                map.put("message", "Data Mismatch! The selected course does not belong to this committee.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(map);
            }
            if(course.getCourseType().equals("Industrial Visit") || course.getCourseType().equals("Thesis") || course.getCourseType().equals("Project") || course.getCourseType().equals("Viva Voce")) {
                map.put("message", "Bad Request: It's Not a Theory or Lab Course! \nExaminer of this type of course can not be assigned in this way. Please try with appropriate procedure.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
            }
            if(course.getCourseTeacher() == null && (course.getCourseType().equals("Theory") || course.getCourseType().equals("Lab"))) {//project/thesis/industrial visit/viva voce courses do not require course teacher
                map.put("message", "Bad Request: Please assign course teacher at first.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
            }

            if(examCommitteeService.checkEditPermission(loggedInUser, committee)) {
                if(courseService.updateQuesSetterAndEvaluator(courseId, internalTeacher, externalTeacher, committee)){
                    map.put("message", "Success: Question Setter & Script Evaluator has been assigned successfully for selected course!");
                    return ResponseEntity.status(HttpStatus.OK).body(map);
                }
                else{
                    map.put("message", "Could not assign question setter and script evaluator for selected course!");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
                }

            }
            else{
                map.put("message", "Forbidden: You are not allowed to perform this action!");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(map);
            }

        }catch (Exception e){
            map.put("status", "Error");
            map.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
        }

    }

}
