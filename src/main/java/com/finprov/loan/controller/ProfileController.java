package com.finprov.loan.controller;

import com.finprov.loan.dto.ApiResponse;
import com.finprov.loan.dto.CustomerProfileRequest;
import com.finprov.loan.entity.Customer;
import com.finprov.loan.entity.User;
import com.finprov.loan.repository.CustomerRepository;
import com.finprov.loan.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Tag(name = "Customer Profile", description = "Endpoints for customer profile management")
public class ProfileController {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    private User currentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @GetMapping
    @Operation(summary = "Get Profile", description = "Get current user's customer profile")
    public ResponseEntity<ApiResponse<Customer>> getProfile() {
        User user = currentUser();
        Customer customer = customerRepository.findByUserId(user.getId())
                .orElse(null);
        return ResponseEntity.ok(ApiResponse.of(true, "Success", customer));
    }

    @PutMapping
    @Operation(summary = "Update Profile", description = "Create or update customer profile")
    public ResponseEntity<ApiResponse<Customer>> updateProfile(@RequestBody CustomerProfileRequest request) {
        User user = currentUser();

        Customer customer = customerRepository.findByUserId(user.getId())
                .orElseGet(() -> Customer.builder().user(user).build());

        if (request.getFirstName() != null)
            customer.setFirstName(request.getFirstName());
        if (request.getLastName() != null)
            customer.setLastName(request.getLastName());
        if (request.getNik() != null)
            customer.setNik(request.getNik());
        if (request.getPhoneNumber() != null)
            customer.setPhoneNumber(request.getPhoneNumber());
        if (request.getAddress() != null)
            customer.setAddress(request.getAddress());
        if (request.getBankName() != null)
            customer.setBankName(request.getBankName());
        if (request.getBankAccountNumber() != null)
            customer.setBankAccountNumber(request.getBankAccountNumber());

        Customer saved = customerRepository.save(customer);
        return ResponseEntity.ok(ApiResponse.of(true, "Profile updated", saved));
    }

    @GetMapping("/check-complete")
    @Operation(summary = "Check Profile Completeness", description = "Check if profile is complete for loan submission")
    public ResponseEntity<ApiResponse<java.util.Map<String, Object>>> checkComplete() {
        User user = currentUser();
        Customer customer = customerRepository.findByUserId(user.getId()).orElse(null);

        boolean isComplete = customer != null
                && customer.getFirstName() != null && !customer.getFirstName().isEmpty()
                && customer.getLastName() != null && !customer.getLastName().isEmpty()
                && customer.getPhoneNumber() != null && !customer.getPhoneNumber().isEmpty()
                && customer.getAddress() != null && !customer.getAddress().isEmpty();

        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("isComplete", isComplete);
        result.put("missingFields", getMissingFields(customer));

        return ResponseEntity.ok(ApiResponse.of(true, "Success", result));
    }

    private java.util.List<String> getMissingFields(Customer customer) {
        java.util.List<String> missing = new java.util.ArrayList<>();
        if (customer == null) {
            missing.add("firstName");
            missing.add("lastName");
            missing.add("phoneNumber");
            missing.add("address");
        } else {
            if (customer.getFirstName() == null || customer.getFirstName().isEmpty())
                missing.add("firstName");
            if (customer.getLastName() == null || customer.getLastName().isEmpty())
                missing.add("lastName");
            if (customer.getPhoneNumber() == null || customer.getPhoneNumber().isEmpty())
                missing.add("phoneNumber");
            if (customer.getAddress() == null || customer.getAddress().isEmpty())
                missing.add("address");
        }
        return missing;
    }
}
