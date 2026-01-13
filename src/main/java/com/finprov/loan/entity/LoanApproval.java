package com.finprov.loan.entity;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.*;

@Entity
@Table(name = "loan_approvals")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanApproval {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "loan_id", nullable = false)
  private Loan loan;

  @Enumerated(EnumType.STRING)
  @Column(name = "status_after", nullable = false, length = 20)
  private LoanStatus statusAfter;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "actor_id", nullable = false)
  private User actor;

  @Column(nullable = false)
  private String role;

  private String remarks;

  @Column(nullable = false)
  private Instant timestamp;
}
