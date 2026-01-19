package com.finprov.loan.dto;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data
@Builder
public class NotificationResponse {
    private Long id;
    private String type;
    private String message;
    private Instant createdAt;
    private boolean read;
}
