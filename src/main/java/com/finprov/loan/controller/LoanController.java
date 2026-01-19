package com.finprov.loan.controller;

import com.finprov.loan.dto.ApiResponse;
import com.finprov.loan.dto.LoanSimulationRequest;
import com.finprov.loan.dto.LoanSimulationResponse;

import com.finprov.loan.dto.SubmitLoanRequest;
import com.finprov.loan.dto.TransitionRequest;
import com.finprov.loan.entity.Loan;
import com.finprov.loan.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
@Tag(name = "Loan Management", description = "Endpoints for loan submission, approval, and disbursement")
public class LoanController {

  private final LoanService loanService;

  @PostMapping
  @PreAuthorize("hasRole('NASABAH')")
  @Operation(summary = "Submit Loan", description = "Submit a new loan application (NASABAH)")
  public ResponseEntity<ApiResponse<Loan>> submitLoan(@RequestBody SubmitLoanRequest request) {
    Loan loan = loanService.submitLoan(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.of(true, "Loan submitted successfully", loan));
  }

  @PostMapping("/simulate")
  @Operation(summary = "Simulate Loan", description = "Calculate loan details without submitting (public)")
  public ResponseEntity<ApiResponse<LoanSimulationResponse>> simulate(@RequestBody LoanSimulationRequest request) {
    LoanSimulationResponse data = loanService.simulateLoan(request);
    return ResponseEntity.ok(ApiResponse.of(true, "Simulation calculated", data));
  }

  @PatchMapping("/{id}/review")
  @PreAuthorize("hasRole('MARKETING')")
  @Operation(summary = "Review Loan", description = "Review a loan application (MARKETING)")
  public ResponseEntity<ApiResponse<Loan>> review(
      @PathVariable Long id, @RequestBody(required = false) TransitionRequest requestBody) {
    Loan data = loanService.reviewLoan(id, requestBody);
    ApiResponse<Loan> body = ApiResponse.of(true, "Reviewed", data);
    return ResponseEntity.ok(body);
  }

  @PatchMapping("/{id}/approve")
  @PreAuthorize("hasRole('BRANCH_MANAGER')")
  @Operation(summary = "Approve Loan", description = "Approve a loan application (BRANCH_MANAGER)")
  public ResponseEntity<ApiResponse<Loan>> approve(
      @PathVariable Long id, @RequestBody(required = false) TransitionRequest requestBody) {
    Loan data = loanService.approveLoan(id, requestBody);
    ApiResponse<Loan> body = ApiResponse.of(true, "Approved", data);
    return ResponseEntity.ok(body);
  }

  @PatchMapping("/{id}/disburse")
  @PreAuthorize("hasRole('BACK_OFFICE')")
  @Operation(summary = "Disburse Loan", description = "Disburse a loan (BACK_OFFICE)")
  public ResponseEntity<ApiResponse<Loan>> disburse(
      @PathVariable Long id, @RequestBody(required = false) TransitionRequest requestBody) {
    Loan data = loanService.disburseLoan(id, requestBody);
    ApiResponse<Loan> body = ApiResponse.of(true, "Disbursed", data);
    return ResponseEntity.ok(body);
  }

  @PatchMapping("/{id}/reject")
  @PreAuthorize("hasAnyRole('BRANCH_MANAGER', 'BACK_OFFICE')")
  @Operation(summary = "Reject Loan", description = "Reject a loan application (BRANCH_MANAGER or BACK_OFFICE)")
  public ResponseEntity<ApiResponse<Loan>> reject(
      @PathVariable Long id, @RequestBody(required = false) TransitionRequest requestBody) {
    Loan data = loanService.rejectLoan(id, requestBody);
    ApiResponse<Loan> body = ApiResponse.of(true, "Rejected", data);
    return ResponseEntity.ok(body);
  }

  @GetMapping
  @Operation(summary = "List Loans", description = "Get all loans")
  public ResponseEntity<ApiResponse<List<Loan>>> list(@RequestParam(required = false) Long branchId) {
    List<Loan> data = loanService.listLoans(branchId);
    ApiResponse<List<Loan>> body = ApiResponse.of(true, "Success", data);
    return ResponseEntity.ok(body);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get Loan Detail", description = "Get details of a specific loan")
  public ResponseEntity<ApiResponse<Loan>> detail(@PathVariable Long id) {
    Loan data = loanService.getLoan(id);
    ApiResponse<Loan> body = ApiResponse.of(true, "Success", data);
    return ResponseEntity.ok(body);
  }
}
