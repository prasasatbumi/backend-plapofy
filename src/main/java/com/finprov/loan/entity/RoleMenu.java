package com.finprov.loan.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "role_menus",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"role_id", "menu_id"})})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleMenu {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "role_id", nullable = false)
  private Role role;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "menu_id", nullable = false)
  private Menu menu;
}
