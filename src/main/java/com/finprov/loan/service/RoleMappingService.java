package com.finprov.loan.service;

public interface RoleMappingService {
  void addPermissionToRole(Long roleId, Long permissionId);

  void removePermissionFromRole(Long rolePermissionId);

  void addMenuToRole(Long roleId, Long menuId);

  void removeMenuFromRole(Long roleMenuId);
}
