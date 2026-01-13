package com.finprov.loan.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtTokenProvider {

  private final String secret;
  private final long expirationMs;
  private volatile Key cachedKey;

  public JwtTokenProvider(
      @Value("${security.jwt.secret:}") String secret,
      @Value("${security.jwt.expiration-ms:3600000}") long expirationMs) {
    this.secret = secret;
    this.expirationMs = expirationMs;
  }

  private Key getKey() {
    if (cachedKey != null)
      return cachedKey;
    if (secret == null || secret.isEmpty()) {
      log.warn("JWT secret is empty; generating ephemeral key");
      cachedKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
      return cachedKey;
    }
    byte[] keyBytes;
    try {
      keyBytes = Decoders.BASE64URL.decode(secret);
      log.debug("Configured JWT secret base64url decoded ({} bytes)", keyBytes.length);
    } catch (RuntimeException e1) {
      try {
        keyBytes = Decoders.BASE64.decode(secret);
        log.debug("Configured JWT secret base64 decoded ({} bytes)", keyBytes.length);
      } catch (RuntimeException e2) {
        log.warn("JWT secret is not valid Base64/Base64URL, using raw bytes");
        keyBytes = secret.getBytes(StandardCharsets.UTF_8);
      }
    }
    if (keyBytes.length < 32) {
      log.warn(
          "JWT key material too short ({} bytes); deriving 256-bit key via SHA-256",
          keyBytes.length);
      try {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        keyBytes = digest.digest(keyBytes);
      } catch (Exception e) {
        log.error("Failed to hash secret; generating ephemeral key", e);
        cachedKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        return cachedKey;
      }
    }
    try {
      cachedKey = Keys.hmacShaKeyFor(keyBytes);
    } catch (Exception e) {
      log.error("Failed to build HMAC key; generating ephemeral key", e);
      cachedKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }
    return cachedKey;
  }

  public String generateToken(String username, Long userId, String role, List<String> roles) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + expirationMs);
    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(now)
        .setExpiration(expiry)
        .claim("userId", userId)
        .claim("role", role)
        .claim("roles", roles)
        .signWith(getKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public String getUsername(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getKey())
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }

  public List<String> getRoles(String token) {
    Object val = Jwts.parserBuilder()
        .setSigningKey(getKey())
        .build()
        .parseClaimsJws(token)
        .getBody()
        .get("roles");
    if (val instanceof List<?> list) {
      return list.stream().map(Object::toString).toList();
    }
    return List.of();
  }

  public Long getUserId(String token) {
    Object val = Jwts.parserBuilder()
        .setSigningKey(getKey())
        .build()
        .parseClaimsJws(token)
        .getBody()
        .get("userId");
    if (val instanceof Number n) {
      return n.longValue();
    }
    if (val instanceof String s) {
      try {
        return Long.parseLong(s);
      } catch (Exception ignored) {
      }
    }
    return null;
  }

  public String getRole(String token) {
    Object val = Jwts.parserBuilder()
        .setSigningKey(getKey())
        .build()
        .parseClaimsJws(token)
        .getBody()
        .get("role");
    return val != null ? val.toString() : null;
  }

  public boolean validate(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
