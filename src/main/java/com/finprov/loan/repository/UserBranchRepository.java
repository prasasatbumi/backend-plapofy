package com.finprov.loan.repository;

import com.finprov.loan.entity.UserBranch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserBranchRepository extends JpaRepository<UserBranch, Long> {
    List<UserBranch> findByBranchCode(String branchCode);

    List<UserBranch> findByBranchId(Long branchId);

    List<UserBranch> findByUserId(Long userId);
}
