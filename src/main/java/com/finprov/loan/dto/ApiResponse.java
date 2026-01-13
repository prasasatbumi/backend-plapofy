package com.finprov.loan.dto;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
  private boolean success;
  private String message;
  private T data;
  private String timestamp;

  public static <T> ApiResponse<T> of(boolean success, String message, T data) {
    return ApiResponse.<T>builder()
        .success(success)
        .message(message)
        .data(data)
        .timestamp(DateTimeFormatter.ISO_INSTANT.format(Instant.now()))
        .build();
  }
}
