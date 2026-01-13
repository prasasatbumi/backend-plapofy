package com.finprov.loan.service;

import com.finprov.loan.entity.Notification;
import java.util.List;

public interface NotificationService {
    List<Notification> getNotificationsForCurrentUser();

    Notification markAsRead(Long id);

    long getUnreadCount();
}
