package com.finprov.loan.controller;

import com.finprov.loan.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
@Tag(name = "Public", description = "Public endpoints")
public class PublicController {

  @GetMapping("/landing")
  @Operation(summary = "Landing Page", description = "Get public landing page info")
  public ResponseEntity<ApiResponse<Map<String, Object>>> landing() {
    Map<String, Object> data =
        Map.of(
            "app", "Loan Management",
            "version", "MVP",
            "message", "Welcome");
    ApiResponse<Map<String, Object>> body = ApiResponse.of(true, "Success", data);
    return ResponseEntity.ok(body);
  }
}
