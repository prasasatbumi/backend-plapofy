package com.finprov.loan.controller;

import com.finprov.loan.dto.ApiResponse;
import com.finprov.loan.service.RoleMappingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/role-mappings")
@RequiredArgsConstructor
@Tag(
    name = "Role Mapping Management",
    description = "Endpoints for managing role-permission and role-menu mappings")
public class RoleMappingController {

  private final RoleMappingService roleMappingService;

  @PostMapping("/roles/{roleId}/permissions/{permissionId}")
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  @Operation(
      summary = "Add Permission to Role",
      description = "Map a permission to a role (SUPER_ADMIN)")
  public ResponseEntity<ApiResponse<Object>> addPermission(
      @PathVariable Long roleId, @PathVariable Long permissionId) {
    roleMappingService.addPermissionToRole(roleId, permissionId);
    ApiResponse<Object> body = ApiResponse.of(true, "Permission mapped", null);
    return ResponseEntity.ok(body);
  }

  @DeleteMapping("/role-permissions/{id}")
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  @Operation(
      summary = "Remove Permission from Role",
      description = "Unmap a permission from a role (SUPER_ADMIN)")
  public ResponseEntity<ApiResponse<Object>> removePermission(@PathVariable Long id) {
    roleMappingService.removePermissionFromRole(id);
    ApiResponse<Object> body = ApiResponse.of(true, "Permission unmapped", null);
    return ResponseEntity.ok(body);
  }

  @PostMapping("/roles/{roleId}/menus/{menuId}")
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  @Operation(summary = "Add Menu to Role", description = "Map a menu to a role (SUPER_ADMIN)")
  public ResponseEntity<ApiResponse<Object>> addMenu(
      @PathVariable Long roleId, @PathVariable Long menuId) {
    roleMappingService.addMenuToRole(roleId, menuId);
    ApiResponse<Object> body = ApiResponse.of(true, "Menu mapped", null);
    return ResponseEntity.ok(body);
  }

  @DeleteMapping("/role-menus/{id}")
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  @Operation(
      summary = "Remove Menu from Role",
      description = "Unmap a menu from a role (SUPER_ADMIN)")
  public ResponseEntity<ApiResponse<Object>> removeMenu(@PathVariable Long id) {
    roleMappingService.removeMenuFromRole(id);
    ApiResponse<Object> body = ApiResponse.of(true, "Menu unmapped", null);
    return ResponseEntity.ok(body);
  }
}
