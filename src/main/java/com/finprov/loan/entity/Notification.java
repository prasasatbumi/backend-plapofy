package com.finprov.loan.entity;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.*;

@Entity
@Table(name = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "notification_type", nullable = false)
  private String type;

  @Column(nullable = false)
  private String message;

  @Column(nullable = false)
  private Instant createdAt;

  @Builder.Default
  @Column(name = "is_read", nullable = false)
  private boolean read = false;
}
