package com.finprov.loan.service.impl;

import com.finprov.loan.dto.LoginRequest;
import com.finprov.loan.dto.LoginResponse;
import com.finprov.loan.dto.RegisterRequest;
import com.finprov.loan.dto.RegisterResponse;
import com.finprov.loan.dto.ResetPasswordRequest;
import com.finprov.loan.entity.Role;
import com.finprov.loan.entity.User;
import com.finprov.loan.repository.RoleRepository;
import com.finprov.loan.repository.UserRepository;
import com.finprov.loan.security.jwt.JwtTokenProvider;
import com.finprov.loan.service.AuthService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;

  @Override
  @Transactional
  public LoginResponse login(LoginRequest request) {
    try {
      log.debug("Login attempt for username={}", request != null ? request.getUsername() : null);
      User user =
          userRepository
              .findByUsername(request.getUsername())
              .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
      boolean enabled = user.getIsActive() != null ? user.getIsActive() : true;
      if (!enabled) {
        throw new IllegalArgumentException("User is disabled");
      }
      if (user.getPassword() == null) {
        throw new IllegalArgumentException("Invalid credentials");
      }
      boolean bcrypt = user.getPassword().startsWith("$2");
      boolean valid =
          bcrypt
              ? passwordEncoder.matches(request.getPassword(), user.getPassword())
              : request.getPassword().equals(user.getPassword());
      if (!valid) throw new IllegalArgumentException("Invalid credentials");
      if (!bcrypt) {
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
      }
      log.debug("User {} authenticated, building JWT", user.getUsername());
      List<String> roles =
          user.getRoles().stream()
              .map(Role::getName)
              .map(r -> "ROLE_" + r)
              .collect(Collectors.toList());
      String primaryRole = user.getRoles().stream().map(Role::getName).findFirst().orElse(null);
      String token;
      try {
        token =
            jwtTokenProvider.generateToken(user.getUsername(), user.getId(), primaryRole, roles);
      } catch (Exception e) {
        log.error("Failed to generate JWT token for user {}", user.getUsername(), e);
        e.printStackTrace(); // FORCE PRINT STACK TRACE
        throw new IllegalStateException("Authentication token generation failed", e);
      }
      return LoginResponse.builder()
          .token(token)
          .username(user.getUsername())
          .userId(user.getId())
          .role(primaryRole)
          .roles(roles)
          .build();
    } catch (Throwable t) {
      log.error("CRITICAL LOGIN FAILURE", t);
      t.printStackTrace(); // FORCE PRINT STACK TRACE TO STDERR
      throw t;
    }
  }

  @Override
  public RegisterResponse register(RegisterRequest request) {
    if (request.getUsername() == null || request.getPassword() == null) {
      throw new IllegalArgumentException("Username and password are required");
    }
    if (userRepository.findByUsername(request.getUsername()).isPresent()) {
      throw new IllegalArgumentException("Username already exists");
    }
    Role userRole =
        roleRepository
            .findByName("NASABAH")
            .orElseThrow(() -> new IllegalStateException("Default role NASABAH not found"));
    User user =
        User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .isActive(true)
            .roles(java.util.Set.of(userRole))
            .build();
    User saved = userRepository.save(user);
    java.util.List<String> roles =
        saved.getRoles().stream().map(Role::getName).map(r -> "ROLE_" + r).toList();
    return RegisterResponse.builder()
        .id(saved.getId())
        .username(saved.getUsername())
        .email(saved.getEmail())
        .roles(roles)
        .build();
  }

  @Override
  public void resetPassword(ResetPasswordRequest request) {
    if (request.getUsername() == null || request.getNewPassword() == null) {
      throw new IllegalArgumentException("Username and newPassword are required");
    }
    User user =
        userRepository
            .findByUsername(request.getUsername())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    userRepository.save(user);
  }
}
