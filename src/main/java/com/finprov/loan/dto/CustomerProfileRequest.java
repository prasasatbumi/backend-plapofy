package com.finprov.loan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerProfileRequest {
    private String firstName;
    private String lastName;
    private String nik;
    private String phoneNumber;
    private String address;
    private String bankName;
    private String bankAccountNumber;
}
