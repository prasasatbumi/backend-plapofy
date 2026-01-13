package com.finprov.loan.service.impl;

import com.finprov.loan.entity.Permission;
import com.finprov.loan.repository.PermissionRepository;
import com.finprov.loan.service.PermissionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

  private final PermissionRepository permissionRepository;

  @Override
  public Permission create(Permission permission) {
    return permissionRepository.save(permission);
  }

  @Override
  public Permission update(Long id, Permission permission) {
    Permission existing =
        permissionRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Permission not found"));
    existing.setCode(permission.getCode());
    existing.setName(permission.getName());
    return permissionRepository.save(existing);
  }

  @Override
  public void delete(Long id) {
    permissionRepository.deleteById(id);
  }

  @Override
  public List<Permission> list() {
    return permissionRepository.findAll();
  }
}
