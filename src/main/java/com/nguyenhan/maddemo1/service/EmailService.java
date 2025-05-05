package com.nguyenhan.maddemo1.service;

import com.nguyenhan.maddemo1.model.Notification;
import com.nguyenhan.maddemo1.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender emailSender;

    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendVerificationEmail(String to, String subject, String text) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true);

        emailSender.send(message);
    }

    public void sendNotificationEmail(String to, Notification notification, User user) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        String subject = notification.getName();
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;\">"
                + "<div style=\"background-color: #ffffff; max-width: 600px; margin: 0 auto; padding: 20px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);\">"
                + "<h2 style=\"color: #333; text-align: center;\">New Notification</h2>"
                + "<p style=\"font-size: 16px; color: #333;\">Hi " + user.getFullName() + ",</p>"
                + "<p style=\"font-size: 16px; color: #333;\">We wanted to inform you that:</p>"
                + "<div style=\"background-color: #f8f8f8; padding: 20px; margin-bottom: 20px; border-radius: 5px;\">"
                + "<h3 style=\"color: #007bff; font-size: 18px;\">Notification Content:</h3>"
                + "<p style=\"font-size: 16px; color: #333;\">" + notification.getContent() + "</p>"
                + "</div>"
                + "<p style=\"font-size: 16px; color: #333;\">" + notification.getCategory() + "</p>"
                + "<p style=\"font-size: 16px; color: #333;\">" + notification.getEventTime() + "</p>"
                + "<p style=\"font-size: 16px; color: #333;\">Please make sure to check the event details in the application.</p>"
                + "</div>"
                + "<div style=\"text-align: center; margin-top: 40px; font-size: 12px; color: #777;\">"
                + "<p>Best regards,</p>"
                + "<p>Your Team</p>"
                + "</div>"
                + "</body>"
                + "</html>";

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlMessage, true);
        emailSender.send(message);
    }

}