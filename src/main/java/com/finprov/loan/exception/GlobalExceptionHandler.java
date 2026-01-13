package com.finprov.loan.exception;

import com.finprov.loan.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiResponse<Object>> handleAccessDenied(AccessDeniedException ex) {
    ApiResponse<Object> body = ApiResponse.of(false, "Access denied: " + ex.getMessage(), null);
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ApiResponse<Object>> handleDataIntegrity(
      DataIntegrityViolationException ex) {
    ApiResponse<Object> body = ApiResponse.of(false, "Data conflict", null);
    return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiResponse<Object>> handleBadRequest(IllegalArgumentException ex) {
    ApiResponse<Object> body = ApiResponse.of(false, ex.getMessage(), null);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Object>> handleGeneric(Exception ex) {
    log.error("Unhandled exception", ex);
    String message = ex.getMessage();
    if (message == null) message = "Internal server error";
    Throwable cause = ex.getCause();
    if (cause != null && cause.getMessage() != null) {
      message += " -> " + cause.getMessage();
    }
    message += " (" + ex.getClass().getName() + ")";
    ApiResponse<Object> body = ApiResponse.of(false, message, null);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
  }
}
