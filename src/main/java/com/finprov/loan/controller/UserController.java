package com.finprov.loan.controller;

import com.finprov.loan.dto.ApiResponse;
import com.finprov.loan.dto.UserDto;
import com.finprov.loan.entity.User;
import com.finprov.loan.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Endpoints for managing users")
public class UserController {

  private final UserService userService;

  @GetMapping
  @org.springframework.security.access.prepost.PreAuthorize("hasRole('SUPER_ADMIN')")
  @Operation(summary = "List Users", description = "Get all users (SUPER_ADMIN)")
  public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers(
      @io.swagger.v3.oas.annotations.Parameter(description = "Filter users by Branch ID") @RequestParam(required = false) Long branchId) {
    List<UserDto> data = userService.getAllUsers(branchId);
    ApiResponse<List<UserDto>> body = ApiResponse.of(true, "Success", data);
    return ResponseEntity.ok(body);
  }

  @PostMapping
  @org.springframework.security.access.prepost.PreAuthorize("hasRole('SUPER_ADMIN')")
  @Operation(summary = "Create User", description = "Create a new user (SUPER_ADMIN)")
  public ResponseEntity<ApiResponse<User>> createUser(@RequestBody User user) {
    User data = userService.createUser(user);
    ApiResponse<User> body = ApiResponse.of(true, "Created", data);
    return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(body);
  }

  @DeleteMapping("/{id}")
  @org.springframework.security.access.prepost.PreAuthorize("hasRole('SUPER_ADMIN')")
  @Operation(summary = "Delete User", description = "Soft delete a user (SUPER_ADMIN)")
  public ResponseEntity<ApiResponse<Object>> deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
    ApiResponse<Object> body = ApiResponse.of(true, "User deleted successfully", null);
    return ResponseEntity.ok(body);
  }

  @PatchMapping("/{id}/status")
  @org.springframework.security.access.prepost.PreAuthorize("hasRole('SUPER_ADMIN')")
  @Operation(summary = "Update User Status", description = "Activate or Deactivate user (SUPER_ADMIN)")
  public ResponseEntity<ApiResponse<Object>> updateStatus(
      @PathVariable Long id, @RequestParam Boolean isActive) {
    userService.updateStatus(id, isActive);
    ApiResponse<Object> body = ApiResponse.of(true, "User status updated", null);
    return ResponseEntity.ok(body);
  }

  @PutMapping("/{id}")
  @org.springframework.security.access.prepost.PreAuthorize("hasRole('SUPER_ADMIN')")
  @Operation(summary = "Update User", description = "Update user details including roles (SUPER_ADMIN)")
  public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable Long id, @RequestBody User user) {
    User updatedUser = userService.updateUser(id, user);
    ApiResponse<User> body = ApiResponse.of(true, "User updated successfully", updatedUser);
    return ResponseEntity.ok(body);
  }

  @PatchMapping("/{id}/branches")
  @org.springframework.security.access.prepost.PreAuthorize("hasRole('SUPER_ADMIN')")
  @Operation(summary = "Assign Branches", description = "Assign branches to Marketing or Branch Manager (SUPER_ADMIN)")
  public ResponseEntity<ApiResponse<User>> assignBranches(
      @PathVariable Long id, @RequestBody java.util.Map<String, java.util.List<Long>> request) {
    java.util.List<Long> branchIds = request.get("branchIds");
    User updatedUser = userService.assignBranches(id, branchIds);
    ApiResponse<User> body = ApiResponse.of(true, "Branches assigned successfully", updatedUser);
    return ResponseEntity.ok(body);
  }
}
