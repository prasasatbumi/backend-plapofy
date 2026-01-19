package com.finprov.loan.pattern.factory;

/**
 * Factory Pattern Example: NotificationFactory
 * 
 * Defines an interface for creating objects, but lets subclasses decide which
 * class to instantiate. Here we use a simple static factory method.
 */
public class NotificationFactory {
    
    public static NotificationService createNotificationService(String type) {
        if (type == null) {
            return null;
        }
        if (type.equalsIgnoreCase("EMAIL")) {
            return new EmailNotificationService();
        } else if (type.equalsIgnoreCase("SMS")) {
            return new SmsNotificationService();
        }
        return null;
    }
}
