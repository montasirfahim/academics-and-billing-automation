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

    @Async
    public void sendModerationEmail(String[] bccRecipients, String subject, String htmlBody, byte[] attachment, String fileName) {
        setupBrevoClient();
        TransactionalEmailsApi apiInstance = new TransactionalEmailsApi();

        SendSmtpEmailSender sender = new SendSmtpEmailSender();
        sender.setEmail(senderEmail);
        sender.setName("Dept of ICT, MBSTU");

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
                       <p style="margin: 0;">Dept. of ICT, MBSTU</p>
                       <p style="margin: 0;">Santosh, Tangail-1902</p>
                </div>
            </div>
            """.formatted(type, otp);

        SendSmtpEmailSender sender = new SendSmtpEmailSender();
        sender.setEmail(senderEmail);
        sender.setName("Dept of ICT, MBSTU");

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

    private void setupBrevoClient() {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        ApiKeyAuth apiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
        apiKeyAuth.setApiKey(apiKey);
    }
}
