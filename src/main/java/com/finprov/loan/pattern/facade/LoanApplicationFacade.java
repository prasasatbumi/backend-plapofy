package com.finprov.loan.pattern.facade;

import com.finprov.loan.pattern.factory.NotificationFactory;
import com.finprov.loan.pattern.factory.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Facade Pattern Example: LoanApplicationFacade
 * 
 * Provides a unified interface to a set of interfaces in a subsystem.
 * Facade defines a higher-level interface that makes the subsystem easier to use.
 */
@Component
public class LoanApplicationFacade {
    
    private static final Logger logger = LoggerFactory.getLogger(LoanApplicationFacade.class);

    // In a real Spring app, these would be injected services
    // For this pattern demo, we mock/simulate them or use the Factory we just made
    
    public void submitLoanApplication(String username, double amount, String productType) {
        logger.info("Starting loan application process for user: {}", username);
        
        // Step 1: Validate User (Simulated)
        if (!validateUser(username)) {
            logger.error("User validation failed");
            return;
        }
        
        // Step 2: Check Credit Score (Simulated)
        if (!checkCreditScore(username)) {
            logger.warn("Credit score too low");
            return;
        }
        
        // Step 3: Create Application (Simulated)
        saveLoanApplication(username, amount, productType);
        
        // Step 4: Send Notification (Using our Factory Pattern)
        NotificationService emailService = NotificationFactory.createNotificationService("EMAIL");
        if (emailService != null) {
            emailService.sendNotification(username + "@example.com", "Your loan application for " + amount + " has been submitted.");
        }
        
        logger.info("Loan application process completed successfully");
    }
    
    private boolean validateUser(String username) {
        logger.info("Validating user: {}", username);
        return true; // Simplified
    }
    
    private boolean checkCreditScore(String username) {
        logger.info("Checking credit score for: {}", username);
        return true; // Simplified
    }
    
    private void saveLoanApplication(String username, double amount, String productType) {
        logger.info("Saving loan application: User={}, Amount={}, Product={}", username, amount, productType);
    }
}
