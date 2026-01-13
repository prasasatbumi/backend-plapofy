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

  @Column(length = 255)
  private String purpose;

  @Column(name = "business_type", length = 100)
  private String businessType;

  @Enumerated(EnumType.STRING)
  @Column(name = "kyc_status", length = 20)
  private KycStatus kycStatus;

  @Column(name = "ktp_image_path", length = 255)
  private String ktpImagePath;

  @Column(name = "selfie_image_path", length = 255)
  private String selfieImagePath;

  @Column(name = "npwp_image_path", length = 255)
  private String npwpImagePath;

  @Column(name = "business_license_image_path", length = 255)
  private String businessLicenseImagePath;

}
