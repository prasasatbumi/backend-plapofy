package com.finprov.loan.security.jwt;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtTokenProviderTest {

  private JwtTokenProvider jwtTokenProvider;
  private String secret = "c2VjdXJlLWFwcC1kZXZlbG9wbWVudC1zZWNyZXQta2V5LTIwMjU=";
  private long expiration = 3600000;

  @BeforeEach
  void setUp() {
    jwtTokenProvider = new JwtTokenProvider(secret, expiration);
  }

  @Test
  void testGenerateToken() {
    String token =
        jwtTokenProvider.generateToken(
            "testuser", 1L, "ROLE_USER", List.of("ROLE_USER", "ROLE_ADMIN"));
    System.out.println("Generated Token: " + token);
    assertNotNull(token);
    assertTrue(token.length() > 0);
  }

  @Test
  void testGenerateTokenWithNullRole() {
    String token = jwtTokenProvider.generateToken("testuser", 1L, null, List.of());
    System.out.println("Generated Token (null role): " + token);
    assertNotNull(token);
  }
}
