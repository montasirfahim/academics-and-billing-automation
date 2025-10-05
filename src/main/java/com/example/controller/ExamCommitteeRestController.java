package com.example.controller;

import com.example.entity.ExamCommittee;
import com.example.entity.User;
import com.example.service.EmailService;
import com.example.service.ExamCommitteeService;
import com.example.service.UtilityService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/committee")
public class ExamCommitteeRestController {
    @Autowired
    private ExamCommitteeService examCommitteeService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UtilityService utilityService;

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
    public ResponseEntity<ExamCommittee> updateCommitteeStatus(@PathVariable Long id, @RequestBody Map<String, Boolean> statusUpdate) {
        Boolean isComplete = statusUpdate.get("isComplete");
        if(isComplete == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            ExamCommittee updatedCommittee = examCommitteeService.updateStatus(id, isComplete);
            return new ResponseEntity<>(updatedCommittee, HttpStatus.OK); // 200 OK
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        }
    }

    @PutMapping("api/moderation/{id}")
    public ResponseEntity<String> callModerationMeeting(@PathVariable Long id, @RequestBody Map<String, String> payload, HttpSession session) throws MessagingException, UnsupportedEncodingException {
        User user =(User) session.getAttribute("user");
        if(user == null) {
            return new ResponseEntity<>("Unauthorized: Please login first.", HttpStatus.UNAUTHORIZED);
        }
        ExamCommittee committee = examCommitteeService.findCommitteeByCommitteeId(id);
        if(!"co-admin".equals(user.getRole()) && !(Objects.equals(user.getUserId(), committee.getChairman().getUserId()))) {
            return  new ResponseEntity<>("Forbidden: You are not allowed to call this meeting", HttpStatus.FORBIDDEN);
        }
        if(committee == null) {
            return new ResponseEntity<>("Committee not found.", HttpStatus.NOT_FOUND);
        }

        if(committee.isModerated()){
            return  new ResponseEntity<>("Already Moderated!", HttpStatus.CONFLICT);
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


        emailService.sendEmail(
                new String[] {"montasirtuhin1128@gmail.com", "nusratbeva1824@gmail.com", "zia@mbstu.ac.bd"},
                "Question Moderation Meeting",
                htmlBody,
                null
        );

        System.out.println("Email sent");
        committee.setModerationCallDateTime(callTime);
        committee.setModerationScheduledDateTime(meetingDateTime);
        committee.setModerated(true);
        examCommitteeService.saveCommittee(committee);

        return new  ResponseEntity<>("Moderation meeting has been called successfully!", HttpStatus.OK);

    }

}
