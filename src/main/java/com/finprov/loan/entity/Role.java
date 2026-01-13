package com.finprov.loan.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.Set;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@SQLDelete(sql = "UPDATE roles SET deleted = 1 WHERE id = ?")
@Where(clause = "deleted = 0 OR deleted IS NULL")
@Table(name = "roles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "role_name", nullable = false, unique = true)
  private String name;

  @ManyToMany(mappedBy = "roles")
  @JsonIgnore
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private Set<User> users;

  @Builder.Default
  @Column(name = "deleted")
  private Boolean deleted = false;
}
