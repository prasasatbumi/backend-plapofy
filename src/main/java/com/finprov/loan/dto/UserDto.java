package com.finprov.loan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
  private Long id;
  private String username;
  private String email;
  private Boolean isActive;
  private java.util.Set<com.finprov.loan.dto.RoleDto> roles;
  private java.util.Set<com.finprov.loan.dto.BranchDto> branches;
}
