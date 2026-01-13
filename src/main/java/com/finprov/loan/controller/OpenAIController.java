package com.finprov.loan.controller;

import com.finprov.loan.dto.ApiResponse;
import com.finprov.loan.dto.OpenAIChatRequest;
import com.finprov.loan.dto.OpenAIChatResponse;
import com.finprov.loan.service.OpenAIService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/openai")
@RequiredArgsConstructor
@Tag(name = "OpenAI", description = "Endpoints for AI chat features")
public class OpenAIController {

  private final OpenAIService openAIService;

  @PostMapping("/chat")
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  @Operation(
      summary = "Chat with AI",
      description = "Send a prompt to OpenAI and get a response (SUPER_ADMIN)")
  public ResponseEntity<ApiResponse<OpenAIChatResponse>> chat(
      @RequestBody OpenAIChatRequest request) {
    OpenAIChatResponse data = openAIService.chat(request);
    ApiResponse<OpenAIChatResponse> body = ApiResponse.of(true, "Success", data);
    return ResponseEntity.ok(body);
  }
}
