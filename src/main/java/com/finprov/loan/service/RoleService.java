package com.finprov.loan.service;

import com.finprov.loan.entity.Role;
import java.util.List;

public interface RoleService {
  Role createRole(Role role);

  List<Role> getAllRoles();

  Role updateRole(Long id, Role role);

  void deleteRole(Long id);

  Role restoreRole(Long id);
}
