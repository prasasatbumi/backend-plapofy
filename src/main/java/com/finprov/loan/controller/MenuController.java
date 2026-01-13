package com.finprov.loan.controller;

import com.finprov.loan.dto.ApiResponse;
import com.finprov.loan.entity.Menu;
import com.finprov.loan.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
@Tag(name = "Menu Management", description = "Endpoints for managing menus")
public class MenuController {

  private final MenuService menuService;

  @GetMapping
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  @Operation(summary = "List Menus", description = "Get all menus (SUPER_ADMIN)")
  public ResponseEntity<ApiResponse<List<Menu>>> list() {
    List<Menu> data = menuService.list();
    ApiResponse<List<Menu>> body = ApiResponse.of(true, "Success", data);
    return ResponseEntity.ok(body);
  }

  @PostMapping
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  @Operation(summary = "Create Menu", description = "Create a new menu (SUPER_ADMIN)")
  public ResponseEntity<ApiResponse<Menu>> create(@RequestBody Menu menu) {
    Menu data = menuService.create(menu);
    ApiResponse<Menu> body = ApiResponse.of(true, "Created", data);
    return ResponseEntity.status(HttpStatus.CREATED).body(body);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  @Operation(summary = "Update Menu", description = "Update an existing menu (SUPER_ADMIN)")
  public ResponseEntity<ApiResponse<Menu>> update(@PathVariable Long id, @RequestBody Menu menu) {
    Menu data = menuService.update(id, menu);
    ApiResponse<Menu> body = ApiResponse.of(true, "Updated", data);
    return ResponseEntity.ok(body);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  @Operation(summary = "Delete Menu", description = "Delete a menu (SUPER_ADMIN)")
  public ResponseEntity<ApiResponse<Object>> delete(@PathVariable Long id) {
    menuService.delete(id);
    ApiResponse<Object> body = ApiResponse.of(true, "Deleted", null);
    return ResponseEntity.ok(body);
  }
}
