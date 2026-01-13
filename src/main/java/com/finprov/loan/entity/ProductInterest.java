package com.finprov.loan.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;

@Entity
@Table(name = "product_interests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductInterest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plafond_id", nullable = false)
    @ToString.Exclude
    private Plafond plafond;

    @Column(nullable = false)
    private Integer tenor; // e.g. 1, 3, 6, 12 months

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal interestRate; // e.g. 1.25 for 1.25%
}
