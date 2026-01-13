package com.finprov.loan.controller;

import com.finprov.loan.dto.ApiResponse;
import com.finprov.loan.entity.Plafond;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/plafonds")
@RequiredArgsConstructor
@Tag(name = "Plafond", description = "Endpoints for loan plafond information")
public class PlafondController {

  private final com.finprov.loan.service.PlafondService plafondService;

  @GetMapping
  @Operation(summary = "List Plafonds", description = "Get list of available loan plafonds (Public)")
  public ResponseEntity<ApiResponse<List<Plafond>>> list() {
    List<Plafond> data = plafondService.findAll();
    ApiResponse<List<Plafond>> body = ApiResponse.of(true, "Success", data);
    return ResponseEntity.ok(body);
  }

  @PostMapping
  @org.springframework.security.access.prepost.PreAuthorize("hasRole('SUPER_ADMIN')")
  @Operation(summary = "Create Plafond", description = "Create new loan package (SUPER_ADMIN)")
  public ResponseEntity<ApiResponse<Plafond>> create(@RequestBody Plafond plafond) {
    Plafond data = plafondService.create(plafond);
    ApiResponse<Plafond> body = ApiResponse.of(true, "Created", data);
    return ResponseEntity.ok(body);
  }

  @PutMapping("/{id}")
  @org.springframework.security.access.prepost.PreAuthorize("hasRole('SUPER_ADMIN')")
  @Operation(summary = "Update Plafond", description = "Update loan package details (SUPER_ADMIN)")
  public ResponseEntity<ApiResponse<Plafond>> update(
      @PathVariable Long id, @RequestBody Plafond plafond) {
    System.out.println("DEBUG: Received UPDATE request for ID: " + id);
    System.out.println("DEBUG: Payload: " + plafond);
    try {
      Plafond data = plafondService.update(id, plafond);
      System.out.println("DEBUG: Update successful");
      ApiResponse<Plafond> body = ApiResponse.of(true, "Updated", data);
      return ResponseEntity.ok(body);
    } catch (Exception e) {
      System.err.println("ERROR: Update failed: " + e.getMessage());
      e.printStackTrace();
      throw e;
    }
  }

  @DeleteMapping("/{id}")
  @org.springframework.security.access.prepost.PreAuthorize("hasRole('SUPER_ADMIN')")
  @Operation(summary = "Delete Plafond", description = "Delete loan package (SUPER_ADMIN)")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
    plafondService.delete(id);
    ApiResponse<Void> body = ApiResponse.of(true, "Deleted", null);
    return ResponseEntity.ok(body);
  }
}
