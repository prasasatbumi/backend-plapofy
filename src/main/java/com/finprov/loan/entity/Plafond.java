package com.finprov.loan.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import lombok.*;

@Entity
@Table(name = "plafonds")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Plafond {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 100)
  private String code;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, length = 500)
  private String description;

  @Column(nullable = false, precision = 18, scale = 2)
  private BigDecimal minAmount;

  @Column(nullable = false, precision = 18, scale = 2)
  private BigDecimal maxAmount;

  @OneToMany(mappedBy = "plafond", cascade = CascadeType.ALL, orphanRemoval = true)
  @com.fasterxml.jackson.annotation.JsonIgnoreProperties("plafond")
  private List<ProductInterest> interests;

  @Column(nullable = false, columnDefinition = "BIT DEFAULT 0")
  @Builder.Default
  private Boolean deleted = false;
}
