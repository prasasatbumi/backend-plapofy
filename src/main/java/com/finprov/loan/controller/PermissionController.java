package com.finprov.loan.controller;

import com.finprov.loan.dto.ApiResponse;
import com.finprov.loan.entity.Permission;
import com.finprov.loan.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
@Tag(name = "Permission Management", description = "Endpoints for managing permissions")
public class PermissionController {

  private final PermissionService permissionService;

  @GetMapping
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  @Operation(summary = "List Permissions", description = "Get all permissions (SUPER_ADMIN)")
  public ResponseEntity<ApiResponse<List<Permission>>> list() {
    List<Permission> data = permissionService.list();
    ApiResponse<List<Permission>> body = ApiResponse.of(true, "Success", data);
    return ResponseEntity.ok(body);
  }

  @PostMapping
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  @Operation(summary = "Create Permission", description = "Create a new permission (SUPER_ADMIN)")
  public ResponseEntity<ApiResponse<Permission>> create(@RequestBody Permission permission) {
    Permission data = permissionService.create(permission);
    ApiResponse<Permission> body = ApiResponse.of(true, "Created", data);
    return ResponseEntity.status(HttpStatus.CREATED).body(body);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  @Operation(summary = "Update Permission", description = "Update a permission (SUPER_ADMIN)")
  public ResponseEntity<ApiResponse<Permission>> update(
      @PathVariable Long id, @RequestBody Permission permission) {
    Permission data = permissionService.update(id, permission);
    ApiResponse<Permission> body = ApiResponse.of(true, "Updated", data);
    return ResponseEntity.ok(body);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  @Operation(summary = "Delete Permission", description = "Delete a permission (SUPER_ADMIN)")
  public ResponseEntity<ApiResponse<Object>> delete(@PathVariable Long id) {
    permissionService.delete(id);
    ApiResponse<Object> body = ApiResponse.of(true, "Deleted", null);
    return ResponseEntity.ok(body);
  }
}
