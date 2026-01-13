package com.finprov.loan.service;

import com.finprov.loan.dto.OpenAIChatRequest;
import com.finprov.loan.dto.OpenAIChatResponse;

public interface OpenAIService {
  OpenAIChatResponse chat(OpenAIChatRequest request);
}
