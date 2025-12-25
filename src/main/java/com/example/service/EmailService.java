package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.io.File;
import java.io.UnsupportedEncodingException;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendEmail(String[] to, String subject, String htmlBody, File attachment)
            throws MessagingException, UnsupportedEncodingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("fahimmontasirtuhin1128@gmail.com", "Notification from Automation Software");
        //helper.setTo(to);
        helper.setBcc(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true); //true = enable HTML

        if(attachment != null) {
            helper.addAttachment(attachment.getName(), attachment);
        }

        mailSender.send(message);
    }

    @Async
    public void sendOTPViaEmail(String to, String subject, String otp, File attachment)
            throws MessagingException, UnsupportedEncodingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        String htmlBody = """
            <p>Your secret one time login verification code is: <b>%s</b></p>
            <p style="color: red;">Please do not share this private code with anyone</p>
            <div style="margin-top: 25px; display:flex; flex-direction: column;">
            <p><b>Regards,</b></p>
            <p>Dept. of ICT, MBSTU</p>
            <p>Santosh, Tangail-1902</p>
            </div>
            """.formatted(otp);


        helper.setFrom("fahimmontasirtuhin1128@gmail.com", "Notification from Automation Software");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);

        if(attachment != null) {
            helper.addAttachment(attachment.getName(), attachment);
        }

        mailSender.send(message);
    }
}

