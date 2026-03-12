package com.example.service;

import brevo.*;
import brevo.auth.*;
import brevoModel.*;
import brevoApi.TransactionalEmailsApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BrevoEmailService {

    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${brevo.sender.email}")
    private String senderEmail;

    @Value("${DEPT}")
    private String deptName;

    @Value("${UNIVERSITY}")
    private String universityName;

    @Value("${UNI_LOCATION}")
    private String universityLocation;

    @Value("${WEB_URL}")
    private String webURL;


    @Async
    public void sendModerationEmail(String[] bccRecipients, String subject, String htmlBody, byte[] attachment, String fileName) {
        setupBrevoClient();
        TransactionalEmailsApi apiInstance = new TransactionalEmailsApi();

        SendSmtpEmailSender sender = new SendSmtpEmailSender();
        sender.setEmail(senderEmail);
        sender.setName("Dept of " + deptName + ", " + universityName);

        SendSmtpEmailTo mainTo = new SendSmtpEmailTo();
        mainTo.setEmail(senderEmail);

        //BCC Recipients
        List<SendSmtpEmailBcc> bccList = Arrays.stream(bccRecipients)
                .map(email -> new SendSmtpEmailBcc().email(email))
                .collect(Collectors.toList());

        List<SendSmtpEmailAttachment> attachments = new ArrayList<>();
        if (attachment != null) {
            try {
                SendSmtpEmailAttachment brevoAttachment = new SendSmtpEmailAttachment();
                brevoAttachment.setName(fileName);
                brevoAttachment.setContent(attachment);
                attachments.add(brevoAttachment);
            } catch (Exception e) {
                System.err.println("Error processing attachment: " + e.getMessage());
            }
        }

        SendSmtpEmail sendSmtpEmail = new SendSmtpEmail();
        sendSmtpEmail.setSender(sender);
        sendSmtpEmail.setTo(Collections.singletonList(mainTo));
        sendSmtpEmail.setBcc(bccList);
        sendSmtpEmail.setSubject(subject);
        sendSmtpEmail.setHtmlContent(htmlBody);
        if (!attachments.isEmpty()) {
            sendSmtpEmail.setAttachment(attachments);
        }

        try {
            apiInstance.sendTransacEmail(sendSmtpEmail);
        } catch (ApiException e) {
            System.err.println("Brevo Email Error: " + e.getResponseBody());
        }
    }


    @Async
    public void sendOTPViaEmail(String toEmail, String subject, String type, String otp) {
        setupBrevoClient();
        TransactionalEmailsApi apiInstance = new TransactionalEmailsApi();

        String htmlBody = """
            <div style="font-family: Arial, sans-serif; padding: 20px; border: 1px solid #eee;">
                <p>Your secret one-time <strong>%s</strong> verification code is:</p>
                <p style="font-size: 24px; letter-spacing: 5px; color: #1e40af; font-weight: bold;">%s</p>
                <p style="color: #991b1b; font-size: 13px;">Please do not share this private code with anyone.</p>
                <hr style="border: 0; border-top: 1px solid #eee; margin: 20px 0;">
                <div style="color: #666; font-size: 13px;">
                       <p style="margin: 0;"><b>Regards,</b></p>
                       <p style="margin: 0;">Dept. of %3$s, %4$s</p>
                       <p style="margin: 0;">%5$s</p>
                </div>
            </div>
            """.formatted(type, otp, deptName, universityName, universityLocation);

        SendSmtpEmailSender sender = new SendSmtpEmailSender();
        sender.setEmail(senderEmail);
        sender.setName("Dept of " + deptName + ", " + universityName);

        SendSmtpEmailTo recipient = new SendSmtpEmailTo();
        recipient.setEmail(toEmail);

        SendSmtpEmail sendSmtpEmail = new SendSmtpEmail();
        sendSmtpEmail.setSender(sender);
        sendSmtpEmail.setTo(Collections.singletonList(recipient));
        sendSmtpEmail.setSubject(subject);
        sendSmtpEmail.setHtmlContent(htmlBody);

        try {
            apiInstance.sendTransacEmail(sendSmtpEmail);
        } catch (ApiException e) {
            System.err.println("Brevo OTP API Error: " + e.getResponseBody());
        }
    }

    @Async
    public void sendWelcomeEmail(String toEmail){
        setupBrevoClient();
        TransactionalEmailsApi apiInstance = new TransactionalEmailsApi();

        SendSmtpEmailSender sender = new SendSmtpEmailSender();
        sender.setEmail(senderEmail);
        sender.setName("Dept of " + deptName + ", " + universityName);

        String htmlBody = """
        <div style="font-family: 'Segoe UI', Arial, sans-serif; max-width: 600px; margin: auto; padding: 30px; border: 1px solid #e5e7eb; border-radius: 12px; color: #374151;">
            <h2 style="color: #111827; margin-top: 0; font-size:22px; border-bottom: 2px solid #3b82f6; display: inline-block; padding-bottom: 4px;">Welcome to Automation Software</h2>
            <p>An account has been successfully created for you at automation software of <strong>Dept. of %1$s, %2$s</strong>.</p>
            
            <div style="background-color: #f9fafb; padding: 20px; border-radius: 8px; margin: 25px 0; border: 1px inset #f3f4f6;">
                <p style="margin: 0; font-size: 12px; color: #6b7280; text-transform: uppercase; font-weight: bold;">Registered Email Address</p>
                <p style="margin: 5px 0 0 0; font-size: 16px; color: #111827; font-family: monospace;">%3$s</p>
            </div>
    
            <p>Please ask the Registrar of the mentioned department <strong>for your temporary password</strong> to login at automation software following the given URL:</p>
            <a href="%4$s" style="display: inline-block; padding: 8px 18px; background-color: #2563eb; color: #ffffff; text-decoration: none; border-radius: 6px; font-weight: 600; margin-bottom: 10px;">Visit Automation Software</a>
           
             <p style="color: #059669; font-weight: 500;">&#9432; You are highly requested to reset a strong password after first login.</p>
    
            <p style="color: #991b1b; background-color: #fef2f2; padding: 10px; border-radius: 4px; font-size: 13px;">
                If you were not expecting this registration, please ignore this email.
            </p>
            <hr style="border: 0; border-top: 1px solid #e5e7eb; margin: 30px 0;">
            <div style="color: #6b7280; font-size: 14px;">
                <p style="margin: 0;"><b>Best regards,</b></p>
                <p style="margin: 0;">Dept. of %1$s, %2$s</p>
                <p style="margin: 0; font-size: 12px; color: #9ca3af;">%5$s</p>
            </div>
        </div>
        """.formatted(deptName, universityName, toEmail, webURL, universityLocation);


        SendSmtpEmailTo recipient = new SendSmtpEmailTo();
        recipient.setEmail(toEmail);

        SendSmtpEmail sendSmtpEmail = new SendSmtpEmail();
        sendSmtpEmail.setSender(sender);
        sendSmtpEmail.setTo(Collections.singletonList(recipient));
        sendSmtpEmail.setSubject("Account Signup Successful");
        sendSmtpEmail.setHtmlContent(htmlBody);

        try{
            apiInstance.sendTransacEmail(sendSmtpEmail);
        }catch(ApiException e){
            System.err.println("Brevo OTP API Error: " + e.getResponseBody());
        }
    }

    private void setupBrevoClient() {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        ApiKeyAuth apiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
        apiKeyAuth.setApiKey(apiKey);
    }
}
