package com.finprov.loan.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanSimulationRequest {
    private Long plafondId;
    private BigDecimal amount;
    private Integer tenorMonth;
}
