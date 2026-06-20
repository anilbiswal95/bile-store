package com.bike.store.common.email;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Map;

/**
 * Service for sending emails.
 * Supports both plain text and HTML emails with Thymeleaf templates.
 */
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    /**
     * Send a simple text email.
     */
    public void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom("noreply@bikestore.com");
        
        try {
            mailSender.send(message);
        } catch (Exception e) {
            // Log error but don't fail the transaction
            System.err.println("Failed to send email to " + to + ": " + e.getMessage());
        }
    }

    /**
     * Send an HTML email using Thymeleaf template.
     * 
     * @param to Recipient email address
     * @param subject Email subject
     * @param templateName Template name without extension (e.g., "order-confirmation")
     * @param variables Variables to pass to template
     */
    public void sendHtmlEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("noreply@bikestore.com");
            
            // Create Thymeleaf context
            Context context = new Context();
            if (variables != null) {
                context.setVariables(variables);
            }
            
            // Process template
            String htmlContent = templateEngine.process("emails/" + templateName, context);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
        } catch (MessagingException e) {
            // Log error but don't fail the transaction
            System.err.println("Failed to send HTML email to " + to + ": " + e.getMessage());
        }
    }
}

