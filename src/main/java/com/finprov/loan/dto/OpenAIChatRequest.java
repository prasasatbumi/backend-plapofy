package com.finprov.loan.dto;

import lombok.Data;

@Data
public class OpenAIChatRequest {
  private String prompt;
  private String model;
  private Double temperature;
}
