package com.finprov.loan.controller;

import com.finprov.loan.dto.ApiResponse;
import com.finprov.loan.dto.NotificationResponse;
import com.finprov.loan.entity.Notification;
import com.finprov.loan.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Endpoints for user notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "List Notifications", description = "Get all notifications for the current user")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> list() {
        List<NotificationResponse> data = notificationService.getNotificationsForCurrentUser();
        return ResponseEntity.ok(ApiResponse.of(true, "Success", data));
    }

    @GetMapping("/unread/count")
    @Operation(summary = "Unread Count", description = "Get count of unread notifications")
    public ResponseEntity<ApiResponse<Map<String, Long>>> unreadCount() {
        long count = notificationService.getUnreadCount();
        return ResponseEntity.ok(ApiResponse.of(true, "Success", Map.of("count", count)));
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark as Read", description = "Mark a notification as read")
    public ResponseEntity<ApiResponse<Notification>> markAsRead(@PathVariable Long id) {
        Notification data = notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.of(true, "Marked as read", data));
    }
}
