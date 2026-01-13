package com.finprov.loan.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class LoanSubmissionWithKycRequest {
    private Long requestedPlafondId;
    private Long branchId;
    private BigDecimal amount;
    private Integer tenorMonth;
    private String purpose;
    private String businessType;
    private String tier;
}
