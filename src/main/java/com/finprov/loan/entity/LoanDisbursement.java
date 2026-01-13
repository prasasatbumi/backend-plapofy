package com.finprov.loan.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.*;

@Entity
@Table(
    name = "loan_disbursements",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"loan_id"})})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanDisbursement {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "loan_id", nullable = false)
  private Loan loan;

  @Column(nullable = false, precision = 18, scale = 2)
  private BigDecimal amount;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "actor_id", nullable = false)
  private User actor;

  @Column(nullable = false)
  private Instant timestamp;
}
