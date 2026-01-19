package com.finprov.loan.pattern.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmsNotificationService implements NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(SmsNotificationService.class);

    @Override
    public void sendNotification(String recipient, String message) {
        logger.info("Sending SMS to {}: {}", recipient, message);
        // Logic to send SMS...
        System.out.println("SMS sent to " + recipient);
    }
}
