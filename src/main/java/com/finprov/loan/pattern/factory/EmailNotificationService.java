package com.finprov.loan.pattern.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailNotificationService implements NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationService.class);

    @Override
    public void sendNotification(String recipient, String message) {
        logger.info("Sending Email to {}: {}", recipient, message);
        // Logic to send email...
        System.out.println("Email sent to " + recipient);
    }
}
