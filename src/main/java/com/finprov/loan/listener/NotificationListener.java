package com.finprov.loan.listener;

import com.finprov.loan.entity.Loan;
import com.finprov.loan.entity.LoanStatus;
import com.finprov.loan.entity.Notification;
import com.finprov.loan.entity.User;
import com.finprov.loan.event.LoanStatusChangedEvent;
import com.finprov.loan.repository.NotificationRepository;
import com.finprov.loan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationListener {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Async
    @EventListener
    public void handleLoanStatusChanged(LoanStatusChangedEvent event) {
        Loan loan = event.getLoan();
        LoanStatus status = event.getStatus();

        switch (status) {
            case SUBMITTED:
                notifyRole("MARKETING", loan.getBranch(), "LOAN_SUBMITTED",
                        "New loan " + loan.getId() + " waiting for review");
                notifyRole("SUPER_ADMIN", null, "LOAN_SUBMITTED",
                        "New loan " + loan.getId() + " submitted");
                break;
            case REVIEWED:
                notifyRole("BRANCH_MANAGER", loan.getBranch(), "LOAN_REVIEWED",
                        "Loan " + loan.getId() + " reviewed, waiting for approval");
                break;
            case APPROVED:
                notify(loan.getApplicant(), "LOAN_APPROVED",
                        "Loan " + loan.getId() + " approved");
                notifyRole("BACK_OFFICE", null, "LOAN_APPROVED",
                        "Loan " + loan.getId() + " approved, ready for disbursement");
                break;
            case DISBURSED:
                notify(loan.getApplicant(), "LOAN_DISBURSED",
                        "Loan " + loan.getId() + " disbursed");
                break;
            case REJECTED:
                notify(loan.getApplicant(), "LOAN_REJECTED",
                        "Loan " + loan.getId() + " has been rejected");
                break;
            default:
                break;
        }
    }

    private void notify(User user, String type, String message) {
        Notification notif = Notification.builder()
                .user(user)
                .type(type)
                .message(message)
                .createdAt(Instant.now())
                .read(false)
                .build();
        notificationRepository.save(notif);
    }

    private void notifyRole(String roleName, com.finprov.loan.entity.Branch branch, String type, String message) {
        List<User> targets;
        if (branch != null) {
            targets = userRepository.findByUserBranches_Branch_IdAndRoles_Name(branch.getId(), roleName);
        } else {
            targets = userRepository.findByRoles_Name(roleName);
        }

        if (targets.isEmpty()) return;

        List<Notification> notifications = targets.stream()
                .map(user -> Notification.builder()
                        .user(user)
                        .type(type)
                        .message(message)
                        .createdAt(Instant.now())
                        .read(false)
                        .build())
                .toList();
        notificationRepository.saveAll(notifications);
    }
}
