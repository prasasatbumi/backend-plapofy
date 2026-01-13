package com.finprov.loan.controller;

import com.finprov.loan.dto.ApiResponse;
import com.finprov.loan.entity.Role;
import com.finprov.loan.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Tag(name = "Role Management", description = "Endpoints for managing roles")
public class RoleController {

  private final RoleService roleService;

  @GetMapping
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  @Operation(summary = "List Roles", description = "Get all roles (SUPER_ADMIN)")
  public ResponseEntity<ApiResponse<List<Role>>> getAllRoles() {
    List<Role> data = roleService.getAllRoles();
    ApiResponse<List<Role>> body = ApiResponse.of(true, "Success", data);
    return ResponseEntity.ok(body);
  }

  @PostMapping
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  @Operation(summary = "Create Role", description = "Create a new role (SUPER_ADMIN)")
  public ResponseEntity<ApiResponse<Role>> createRole(@RequestBody Role role) {
    Role data = roleService.createRole(role);
    ApiResponse<Role> body = ApiResponse.of(true, "Created", data);
    return ResponseEntity.status(HttpStatus.CREATED).body(body);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  @Operation(summary = "Update Role", description = "Update an existing role (SUPER_ADMIN)")
  public ResponseEntity<ApiResponse<Role>> updateRole(
      @PathVariable Long id, @RequestBody Role role) {
    Role data = roleService.updateRole(id, role);
    ApiResponse<Role> body = ApiResponse.of(true, "Updated", data);
    return ResponseEntity.ok(body);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  @Operation(summary = "Delete Role", description = "Soft delete a role (SUPER_ADMIN)")
  public ResponseEntity<ApiResponse<Object>> deleteRole(@PathVariable Long id) {
    roleService.deleteRole(id);
    ApiResponse<Object> body = ApiResponse.of(true, "Deleted", null);
    return ResponseEntity.ok(body);
  }

  @PutMapping("/{id}/restore")
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  @Operation(summary = "Restore Role", description = "Restore a deleted role (SUPER_ADMIN)")
  public ResponseEntity<ApiResponse<Role>> restoreRole(@PathVariable Long id) {
    Role data = roleService.restoreRole(id);
    ApiResponse<Role> body = ApiResponse.of(true, "Restored", data);
    return ResponseEntity.ok(body);
  }
}
