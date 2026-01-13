package com.finprov.loan.controller;

import com.finprov.loan.dto.ApiResponse;
import com.finprov.loan.entity.Branch;
import com.finprov.loan.repository.BranchRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/branches")
@RequiredArgsConstructor
@Tag(name = "Branch Management", description = "Endpoints for managing corporate branches")
public class BranchController {

    private final BranchRepository branchRepository;
    private final com.finprov.loan.repository.UserRepository userRepository;

    @GetMapping
    @Operation(summary = "List Active Branches", description = "Get all active branches including manager info and marketing counts")
    public ResponseEntity<ApiResponse<List<com.finprov.loan.dto.BranchDto>>> list() {
        List<Branch> branches = branchRepository.findAllActive();
        List<com.finprov.loan.dto.BranchDto> data = branches.stream().map(b -> {
            java.util.List<com.finprov.loan.dto.BranchDto.StaffDto> branchManagers = userRepository
                    .findByUserBranches_Branch_IdAndRoles_Name(b.getId(), "BRANCH_MANAGER")
                    .stream()
                    .map(u -> com.finprov.loan.dto.BranchDto.StaffDto.builder()
                            .username(u.getUsername())
                            .email(u.getEmail())
                            .joinedAt(u.getUserBranches().stream()
                                    .filter(ub -> ub.getBranch().getId().equals(b.getId()))
                                    .findFirst()
                                    .map(com.finprov.loan.entity.UserBranch::getAssignedAt)
                                    .orElse(u.getCreatedAt()))
                            .build())
                    .collect(java.util.stream.Collectors.toList());

            java.util.List<com.finprov.loan.dto.BranchDto.StaffDto> marketingStaff = userRepository
                    .findByUserBranches_Branch_IdAndRoles_Name(b.getId(), "MARKETING")
                    .stream()
                    .map(u -> com.finprov.loan.dto.BranchDto.StaffDto.builder()
                            .username(u.getUsername())
                            .email(u.getEmail())
                            .joinedAt(u.getUserBranches().stream()
                                    .filter(ub -> ub.getBranch().getId().equals(b.getId()))
                                    .findFirst()
                                    .map(com.finprov.loan.entity.UserBranch::getAssignedAt)
                                    .orElse(u.getCreatedAt()))
                            .build())
                    .collect(java.util.stream.Collectors.toList());

            Long marketingCount = (long) marketingStaff.size();

            return com.finprov.loan.dto.BranchDto.builder()
                    .id(b.getId())
                    .code(b.getCode())
                    .name(b.getName())
                    .branchManagers(branchManagers)
                    .marketingStaff(marketingStaff)
                    .marketingCount(marketingCount)
                    .build();
        }).collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(ApiResponse.of(true, "Success", data));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get branch by ID", description = "Get a specific branch by ID")
    public ResponseEntity<ApiResponse<Branch>> getById(@PathVariable Long id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Branch not found"));
        return ResponseEntity.ok(ApiResponse.of(true, "Success", branch));
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Create branch", description = "Create a new branch (SUPER_ADMIN only)")
    public ResponseEntity<ApiResponse<Branch>> create(@RequestBody Branch branch) {
        if (branch.getCode() == null || branch.getCode().isBlank()) {
            throw new IllegalArgumentException("Branch code is required");
        }
        if (branch.getName() == null || branch.getName().isBlank()) {
            throw new IllegalArgumentException("Branch name is required");
        }
        if (branchRepository.findByCode(branch.getCode()).isPresent()) {
            throw new IllegalArgumentException("Branch code already exists");
        }
        branch.setId(null);
        branch.setDeleted(false);
        Branch saved = branchRepository.save(branch);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(true, "Branch created", saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Update branch", description = "Update an existing branch (SUPER_ADMIN only)")
    public ResponseEntity<ApiResponse<Branch>> update(@PathVariable Long id, @RequestBody Branch branch) {
        Branch existing = branchRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Branch not found"));

        if (branch.getCode() != null && !branch.getCode().isBlank()) {
            // Check if code is being changed and if new code already exists
            if (!existing.getCode().equals(branch.getCode())
                    && branchRepository.findByCode(branch.getCode()).isPresent()) {
                throw new IllegalArgumentException("Branch code already exists");
            }
            existing.setCode(branch.getCode());
        }
        if (branch.getName() != null && !branch.getName().isBlank()) {
            existing.setName(branch.getName());
        }
        if (branch.getAddress() != null) {
            existing.setAddress(branch.getAddress());
        }

        Branch saved = branchRepository.save(existing);
        return ResponseEntity.ok(ApiResponse.of(true, "Branch updated", saved));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Delete branch", description = "Soft delete a branch (SUPER_ADMIN only)")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable Long id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Branch not found"));
        branchRepository.delete(branch); // Uses @SQLDelete for soft delete
        return ResponseEntity.ok(ApiResponse.of(true, "Branch deleted", null));
    }
}
