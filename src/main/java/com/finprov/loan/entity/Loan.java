package com.finprov.loan.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.*;

@Entity
@Table(name = "loans")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Loan {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "applicant_id", nullable = false)
  private User applicant;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "plafond_id", nullable = false)
  private Plafond plafond;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "branch_id", nullable = false)
  private Branch branch;

  @Column(nullable = false, precision = 18, scale = 2)
  private BigDecimal amount;

  @Column(nullable = false)
  private Integer tenor;

  @Column(nullable = false, precision = 5, scale = 2)
  private BigDecimal interestRate;

  @Column(name = "monthly_installment", nullable = false, precision = 18, scale = 2)
  private BigDecimal monthlyInstallment;

  @Enumerated(EnumType.STRING)
  @Column(name = "current_status", nullable = false, length = 20)
  private LoanStatus currentStatus;

  @Column(nullable = false)
  private Instant createdAt;

  @Column(name = "disbursed_at")
  private Instant disbursedAt;

  @Column(length = 255)
  private String purpose;

  @Column(name = "business_type", length = 100)
  private String businessType;

  @Column(name = "ktp_image_path", length = 255)
  private String ktpImagePath;

  @Transient
  public java.time.LocalDate getNextDueDate() {
    if (disbursedAt == null) return null;
    java.time.LocalDate startDate = disbursedAt.atZone(java.time.ZoneId.systemDefault()).toLocalDate();
    java.time.LocalDate now = java.time.LocalDate.now();
    // Assuming due date is same day of month as disbursement
    java.time.LocalDate nextDue = startDate.withMonth(now.getMonthValue());
    if (nextDue.isBefore(now)) {
        nextDue = nextDue.plusMonths(1);
    }
    return nextDue;
  }

  @Transient
  public Integer getRemainingTenor() {
      if (disbursedAt == null || tenor == null) return null;
      java.time.LocalDate startDate = disbursedAt.atZone(java.time.ZoneId.systemDefault()).toLocalDate();
      java.time.LocalDate now = java.time.LocalDate.now();
      long monthsPassed = java.time.temporal.ChronoUnit.MONTHS.between(startDate, now);
      return Math.max(0, tenor - (int) monthsPassed);
  }
}
