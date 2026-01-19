package com.finprov.loan.controller;

import com.finprov.loan.dto.ApiResponse;
import com.finprov.loan.entity.Customer;
import com.finprov.loan.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import com.finprov.loan.dto.TransitionRequest;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Customer Management", description = "Endpoints for managing customers")
public class CustomerController {

  private final CustomerService customerService;

  @GetMapping
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  @Operation(summary = "List Customers", description = "Get all customers (SUPER_ADMIN)")
  public ResponseEntity<ApiResponse<List<Customer>>> getAllCustomers() {
    List<Customer> data = customerService.getAllCustomers();
    ApiResponse<List<Customer>> body = ApiResponse.of(true, "Success", data);
    return ResponseEntity.ok(body);
  }

  @PostMapping
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  @Operation(summary = "Create Customer", description = "Create a new customer (SUPER_ADMIN)")
  public ResponseEntity<ApiResponse<Customer>> createCustomer(@RequestBody Customer customer) {
    Customer data = customerService.createCustomer(customer);
    ApiResponse<Customer> body = ApiResponse.of(true, "Created", data);
    return ResponseEntity.status(HttpStatus.CREATED).body(body);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  @Operation(summary = "Delete Customer", description = "Soft delete a customer (SUPER_ADMIN)")
  public ResponseEntity<ApiResponse<Object>> deleteCustomer(@PathVariable Long id) {
    customerService.deleteCustomer(id);
    ApiResponse<Object> body = ApiResponse.of(true, "Customer deleted successfully", null);
    return ResponseEntity.ok(body);
  }

  @PostMapping(value = "/kyc", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasRole('NASABAH')")
  @Operation(summary = "Submit KYC", description = "Submit KYC documents (NASABAH)")
  public ResponseEntity<ApiResponse<Customer>> submitKyc(
      @RequestPart("ktpImage") MultipartFile ktpImage,
      @RequestPart("selfieImage") MultipartFile selfieImage,
      @RequestPart(value = "npwpImage", required = false) MultipartFile npwpImage,
      @RequestPart(value = "businessLicenseImage", required = false) MultipartFile businessLicenseImage) {

    Customer data = customerService.submitKyc(ktpImage, selfieImage, npwpImage, businessLicenseImage);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.of(true, "KYC Submitted successfully", data));
  }

  @PatchMapping("/{id}/verify-kyc")
  @PreAuthorize("hasAnyRole('BACK_OFFICE', 'SUPER_ADMIN')")
  @Operation(summary = "Verify KYC", description = "Approve or Reject KYC (BACK_OFFICE)")
  public ResponseEntity<ApiResponse<Customer>> verifyKyc(
      @PathVariable Long id,
      @RequestParam boolean approve,
      @RequestBody(required = false) TransitionRequest request) {

    String remarks = request != null ? request.getRemarks() : null;
    Customer data = customerService.verifyKyc(id, approve, remarks);
    String status = approve ? "Approved" : "Rejected";
    return ResponseEntity.ok(ApiResponse.of(true, "KYC " + status, data));
  }
}
