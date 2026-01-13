package com.finprov.loan.service.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.finprov.loan.dto.LoginRequest;
import com.finprov.loan.dto.LoginResponse;
import com.finprov.loan.entity.Role;
import com.finprov.loan.entity.User;
import com.finprov.loan.repository.RoleRepository;
import com.finprov.loan.repository.UserRepository;
import com.finprov.loan.security.jwt.JwtTokenProvider;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

  @Mock private UserRepository userRepository;
  @Mock private RoleRepository roleRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private JwtTokenProvider jwtTokenProvider;

  @InjectMocks private AuthServiceImpl authService;

  @Test
  void testLoginSuccess() {
    User user = new User();
    user.setId(1L);
    user.setUsername("testuser");
    user.setPassword("$2a$10$encodedPassword"); // Starts with $2 so it triggers bcrypt check
    user.setIsActive(true);
    Role role = new Role();
    role.setName("USER");
    user.setRoles(Set.of(role));

    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("password", "$2a$10$encodedPassword")).thenReturn(true);
    when(jwtTokenProvider.generateToken(any(), any(), any(), any())).thenReturn("token");

    LoginRequest request = new LoginRequest();
    request.setUsername("testuser");
    request.setPassword("password");

    LoginResponse response = authService.login(request);
    assertNotNull(response);
  }
}
