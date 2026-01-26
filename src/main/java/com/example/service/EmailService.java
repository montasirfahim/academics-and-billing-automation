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
    public void sendOTPViaEmail(String to, String subject,String type, String otp)
            throws MessagingException, UnsupportedEncodingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        String htmlBody = """
            <div style="font-family: Arial, sans-serif; padding: 20px; border: 1px solid #eee;">
                <p>Your secret one-time <strong>%s</strong> verification code is:</p>
                <p style="font-size: 24px; letter-spacing: 5px; color: #1e40af; font-weight: bold;">%s</p>
                <p style="color: #991b1b; font-size: 13px;">Please do not share this private code with anyone.</p>
                <hr style="border: 0; border-top: 1px solid #eee; margin: 20px 0;">
                <div style="color: #666; font-size: 13px;">
                       <p style="margin: 0;"><b>Regards,</b></p>
                       <p style="margin: 0;">Dept. of ICT, MBSTU</p>
                       <p style="margin: 0;">Santosh, Tangail-1902</p>
                </div>
            </div>
            """.formatted(type, otp);


        helper.setFrom("fahimmontasirtuhin1128@gmail.com", "Dept of ICT, MBSTU");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);

        mailSender.send(message);
    }
}

