package com.finprov.loan.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.Set;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "users")
@SQLDelete(sql = "UPDATE users SET deleted = 1 WHERE id = ?")
@Where(clause = "deleted = 0 OR deleted IS NULL")
@EntityListeners(org.springframework.data.jpa.domain.support.AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(unique = true)
  private String email;

  @JsonIgnore
  private String password;

  @Builder.Default
  private Boolean isActive = true;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private Set<Role> roles;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JsonIgnoreProperties("user")
  @ToString.Exclude
  private Customer customer;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @Builder.Default
  private Set<UserBranch> userBranches = new java.util.HashSet<>();

  // Helper getters/setters for backward compatibility
  public Set<Branch> getBranches() {
    if (userBranches == null)
      return new java.util.HashSet<>();
    return userBranches.stream()
        .map(UserBranch::getBranch)
        .collect(java.util.stream.Collectors.toSet());
  }

  public void setBranches(Set<Branch> branches) {
    if (this.userBranches == null) {
      this.userBranches = new java.util.HashSet<>();
    }
    this.userBranches.clear();
    if (branches != null) {
      branches.forEach(branch -> {
        UserBranch ub = UserBranch.builder()
            .user(this)
            .branch(branch)
            .assignedAt(java.time.Instant.now())
            .build();
        this.userBranches.add(ub);
      });
    }
  }

  @Column(name = "created_at", nullable = true, updatable = false)
  @org.springframework.data.annotation.CreatedDate
  private java.time.Instant createdAt;

  @Column(name = "updated_at")
  @org.springframework.data.annotation.LastModifiedDate
  private java.time.Instant updatedAt;

  @Builder.Default
  @Column(name = "deleted")
  private Boolean deleted = false;
}
