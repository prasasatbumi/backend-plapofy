package com.finprov.loan.repository;

import com.finprov.loan.entity.RolePermission;
import com.finprov.loan.entity.Permission;
import com.finprov.loan.entity.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    Optional<RolePermission> findByRoleAndPermission(Role role, Permission permission);
}
