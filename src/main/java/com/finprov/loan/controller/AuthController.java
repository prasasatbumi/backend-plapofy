package com.finprov.loan.controller;

import com.finprov.loan.dto.ApiResponse;
import com.finprov.loan.dto.LoginRequest;
import com.finprov.loan.dto.LoginResponse;
import com.finprov.loan.dto.RegisterRequest;
import com.finprov.loan.dto.RegisterResponse;
import com.finprov.loan.dto.ResetPasswordRequest;
import com.finprov.loan.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user authentication")
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  @Operation(summary = "Login user", description = "Authenticates a user and returns a JWT token")
  public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest requestBody) {
    LoginResponse data = authService.login(requestBody);
    ApiResponse<LoginResponse> body = ApiResponse.of(true, "Authenticated", data);
    return ResponseEntity.ok(body);
  }

  @PostMapping("/register")
  @Operation(
      summary = "Register user",
      description = "Registers a new user (default role: NASABAH)")
  public ResponseEntity<ApiResponse<RegisterResponse>> register(
      @RequestBody RegisterRequest requestBody) {
    RegisterResponse data = authService.register(requestBody);
    ApiResponse<RegisterResponse> body = ApiResponse.of(true, "Registered", data);
    return ResponseEntity.status(HttpStatus.CREATED).body(body);
  }

  @PostMapping("/reset-password")
  @Operation(summary = "Reset password", description = "Resets user password")
  public ResponseEntity<ApiResponse<Object>> resetPassword(
      @RequestBody ResetPasswordRequest requestBody) {
    authService.resetPassword(requestBody);
    ApiResponse<Object> body = ApiResponse.of(true, "Password reset", null);
    return ResponseEntity.ok(body);
  }

  @PostMapping("/logout")
  @Operation(summary = "Logout user", description = "Logs out the current user")
  public ResponseEntity<ApiResponse<Object>> logout() {
    ApiResponse<Object> body = ApiResponse.of(true, "Logged out", null);
    return ResponseEntity.ok(body);
  }
}
