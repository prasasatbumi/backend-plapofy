package com.finprov.loan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchDto {
    private Long id;
    private String code;
    private String name;
    private java.util.List<StaffDto> branchManagers;
    private java.util.List<StaffDto> marketingStaff;
    private Long marketingCount; // Keep for backward compatibility or quick display

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StaffDto {
        private String username;
        private String email;
        private java.time.Instant joinedAt;
    }
}
