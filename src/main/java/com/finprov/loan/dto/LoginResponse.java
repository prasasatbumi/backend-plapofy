package com.finprov.loan.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
  private String token;
  private String username;
  private Long userId;
  private String role;
  private List<String> roles;
}
