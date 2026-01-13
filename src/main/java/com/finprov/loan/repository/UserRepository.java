package com.finprov.loan.repository;

import com.finprov.loan.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);

  java.util.List<User> findByUserBranches_Branch_IdAndRoles_Name(Long branchId, String roleName);

  Long countByUserBranches_Branch_IdAndRoles_Name(Long branchId, String roleName);

  java.util.List<User> findByUserBranches_Branch_IdAndDeletedFalse(Long branchId);
}
