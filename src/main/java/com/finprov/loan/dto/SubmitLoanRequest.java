package com.finprov.loan.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class SubmitLoanRequest {
  private Long plafondId;
  private Long branchId;
  private BigDecimal amount;
  private Integer tenor;
}
