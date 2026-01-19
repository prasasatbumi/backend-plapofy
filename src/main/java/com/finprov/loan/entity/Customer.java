package com.finprov.loan.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.io.Serializable;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "customers")
@SQLDelete(sql = "UPDATE customers SET deleted = 1 WHERE id = ?")
@Where(clause = "deleted = 0 OR deleted IS NULL")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne
  @JoinColumn(name = "user_id", unique = true)
  @JsonIgnore
  private User user;

  @Column(nullable = false)
  private String firstName;

  @Column(nullable = false)
  private String lastName;

  @Column(unique = true)
  private String nik;

  private String phoneNumber;

  private String address;

  @Enumerated(EnumType.STRING)
  private KycStatus kycStatus;

  private String ktpImagePath;
  private String selfieImagePath;
  private String npwpImagePath;
  private String businessLicenseImagePath;

  @Column(name = "bank_name")
  private String bankName;

  @Column(name = "bank_account_number")
  private String bankAccountNumber;

  @Builder.Default
  @Column(name = "deleted")
  private Boolean deleted = false;
}
