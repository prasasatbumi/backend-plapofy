package com.finprov.loan.service.impl;

import com.finprov.loan.entity.Notification;
import com.finprov.loan.entity.User;
import com.finprov.loan.repository.NotificationRepository;
import com.finprov.loan.repository.UserRepository;
import com.finprov.loan.service.NotificationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    private User currentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Override
    public List<Notification> getNotificationsForCurrentUser() {
        User user = currentUser();
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    @Override
    public Notification markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

        // Verify ownership
        User user = currentUser();
        if (!notification.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Unauthorized access to notification");
        }

        notification.setRead(true);
        return notificationRepository.save(notification);
    }

    @Override
    public long getUnreadCount() {
        User user = currentUser();
        return notificationRepository.countByUserIdAndReadFalse(user.getId());
    }
}
