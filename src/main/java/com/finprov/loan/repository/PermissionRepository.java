package com.finprov.loan.repository;

import com.finprov.loan.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    java.util.Optional<Permission> findByCode(String code);
}
