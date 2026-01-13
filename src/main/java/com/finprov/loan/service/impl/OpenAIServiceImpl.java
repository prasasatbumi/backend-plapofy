package com.finprov.loan.service.impl;

import com.finprov.loan.dto.OpenAIChatRequest;
import com.finprov.loan.dto.OpenAIChatResponse;
import com.finprov.loan.service.OpenAIService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class OpenAIServiceImpl implements OpenAIService {

  private final RestTemplate restTemplate;

  @Value("${openai.api.key:}")
  private String apiKey;

  @Value("${openai.api.base-url:https://api.openai.com/v1}")
  private String baseUrl;

  @Override
  public OpenAIChatResponse chat(OpenAIChatRequest request) {
    String model = request.getModel() != null ? request.getModel() : "gpt-4o-mini";
    Double temperature = request.getTemperature() != null ? request.getTemperature() : 0.7;
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(apiKey);
    Map<String, Object> body =
        Map.of(
            "model", model,
            "messages", java.util.List.of(Map.of("role", "user", "content", request.getPrompt())),
            "temperature", temperature);
    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
    Map<?, ?> response =
        restTemplate.postForObject(baseUrl + "/chat/completions", entity, Map.class);
    String content = null;
    try {
      Object choices = response.get("choices");
      if (choices instanceof java.util.List<?> list && !list.isEmpty()) {
        Object first = list.get(0);
        if (first instanceof Map<?, ?> m) {
          Object message = m.get("message");
          if (message instanceof Map<?, ?> msg) {
            Object c = msg.get("content");
            if (c != null) content = c.toString();
          }
        }
      }
    } catch (Exception ignored) {
    }
    if (content == null) content = String.valueOf(response);
    return OpenAIChatResponse.builder().content(content).build();
  }
}
