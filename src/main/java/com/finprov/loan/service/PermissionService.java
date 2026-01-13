package com.finprov.loan.service;

import com.finprov.loan.entity.Permission;
import java.util.List;

public interface PermissionService {
  Permission create(Permission permission);

  Permission update(Long id, Permission permission);

  void delete(Long id);

  List<Permission> list();
}
