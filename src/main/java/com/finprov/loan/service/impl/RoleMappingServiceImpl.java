package com.finprov.loan.service.impl;

import com.finprov.loan.entity.*;
import com.finprov.loan.repository.*;
import com.finprov.loan.service.RoleMappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleMappingServiceImpl implements RoleMappingService {

  private final RoleRepository roleRepository;
  private final PermissionRepository permissionRepository;
  private final RolePermissionRepository rolePermissionRepository;
  private final MenuRepository menuRepository;
  private final RoleMenuRepository roleMenuRepository;

  @Override
  public void addPermissionToRole(Long roleId, Long permissionId) {
    Role role =
        roleRepository
            .findById(roleId)
            .orElseThrow(() -> new IllegalArgumentException("Role not found"));
    Permission permission =
        permissionRepository
            .findById(permissionId)
            .orElseThrow(() -> new IllegalArgumentException("Permission not found"));
    RolePermission rp = RolePermission.builder().role(role).permission(permission).build();
    rolePermissionRepository.save(rp);
  }

  @Override
  public void removePermissionFromRole(Long rolePermissionId) {
    rolePermissionRepository.deleteById(rolePermissionId);
  }

  @Override
  public void addMenuToRole(Long roleId, Long menuId) {
    Role role =
        roleRepository
            .findById(roleId)
            .orElseThrow(() -> new IllegalArgumentException("Role not found"));
    Menu menu =
        menuRepository
            .findById(menuId)
            .orElseThrow(() -> new IllegalArgumentException("Menu not found"));
    RoleMenu rm = RoleMenu.builder().role(role).menu(menu).build();
    roleMenuRepository.save(rm);
  }

  @Override
  public void removeMenuFromRole(Long roleMenuId) {
    roleMenuRepository.deleteById(roleMenuId);
  }
}
