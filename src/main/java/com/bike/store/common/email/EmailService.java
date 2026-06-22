package com.bike.store.common.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;  // CHANGED: Add this import
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j  // CHANGED: Added @Slf4j for logging
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public void sendSimpleEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            message.setFrom("noreply@bobbyridecustoms.com");  // CHANGED: Updated domain

            mailSender.send(message);
            log.info("Simple email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send simple email to {}: {}", to, e.getMessage(), e);
        }
    }

    public void sendHtmlEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("noreply@bobbyridecustoms.com");  // CHANGED: Updated domain

            Context context = new Context();
            if (variables != null) {
                context.setVariables(variables);
            }

            // CHANGED: Try different template paths
            String htmlContent;
            try {
                // Try with "emails/" prefix first
                htmlContent = templateEngine.process("emails/" + templateName, context);
            } catch (Exception e1) {
                log.warn("Template not found in 'emails/' folder, trying root: {}", e1.getMessage());
                try {
                    // Try without prefix (root templates folder)
                    htmlContent = templateEngine.process(templateName, context);
                } catch (Exception e2) {
                    log.error("Template not found: {}", templateName);
                    // Create fallback HTML content
                    htmlContent = createFallbackEmailContent(variables);
                }
            }

            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("HTML email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send HTML email to {}: {}", to, e.getMessage(), e);
        }
    }

    // CHANGED: Added fallback method when template is not found
    private String createFallbackEmailContent(Map<String, Object> variables) {
        StringBuilder html = new StringBuilder();
        html.append("<html><body>");
        html.append("<h1>Order Confirmation - BOBBY RIDE CUSTOMS</h1>");
        html.append("<p>Dear ").append(variables.getOrDefault("customerName", "Customer")).append(",</p>");
        html.append("<p>Thank you for your order! Order #").append(variables.getOrDefault("orderNumber", "N/A")).append("</p>");
        html.append("<p>Total Amount: ₹").append(variables.getOrDefault("totalAmount", "0.00")).append("</p>");
        html.append("<p>Shipping Address: ").append(variables.getOrDefault("shippingAddress", "N/A")).append("</p>");
        html.append("<p>Payment Method: ").append(variables.getOrDefault("paymentMethod", "COD")).append("</p>");
        html.append("<p>We'll send you a tracking number once your order ships.</p>");
        html.append("</body></html>");
        return html.toString();
    }
}