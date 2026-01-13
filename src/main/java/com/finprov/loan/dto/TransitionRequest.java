package com.finprov.loan.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class TransitionRequest {
  private String remarks;
  private BigDecimal amount;
}
