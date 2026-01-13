package com.finprov.loan.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OpenAIChatResponse {
  private String content;
}
