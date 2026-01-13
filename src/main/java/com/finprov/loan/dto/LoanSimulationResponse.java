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
public class LoanSimulationResponse {
    private BigDecimal requestedAmount;
    private Integer tenorMonth;
    private BigDecimal interestRate;
    private BigDecimal monthlyInstallment;
    private BigDecimal totalInterest;
    private BigDecimal totalPayment;
    private BigDecimal adminFee;
    private BigDecimal netDisbursement;
    private String plafondName;
}
