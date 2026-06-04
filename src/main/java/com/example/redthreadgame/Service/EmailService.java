package com.example.redthreadgame.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private static final Logger logger = Logger.getLogger(EmailService.class.getName());

    public void send(String to, String subject, String body) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(body);
            mailSender.send(msg);
            logger.info("Email effectively dispatched to: " + to);
        } catch (Exception e) {
            logger.severe("CRITICAL: Failed to transmit email to " + to + " due to: " + e.getMessage());
        }
    }
}
