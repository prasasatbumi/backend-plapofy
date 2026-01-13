package com.finprov.loan.service;

import com.finprov.loan.dto.LoginRequest;
import com.finprov.loan.dto.LoginResponse;
import com.finprov.loan.dto.RegisterRequest;
import com.finprov.loan.dto.RegisterResponse;
import com.finprov.loan.dto.ResetPasswordRequest;

public interface AuthService {
  LoginResponse login(LoginRequest request);

  RegisterResponse register(RegisterRequest request);

  void resetPassword(ResetPasswordRequest request);
}
