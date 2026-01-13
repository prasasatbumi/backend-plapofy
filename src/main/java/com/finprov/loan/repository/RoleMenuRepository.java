package com.finprov.loan.repository;

import com.finprov.loan.entity.RoleMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleMenuRepository extends JpaRepository<RoleMenu, Long> {}
