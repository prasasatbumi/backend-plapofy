package com.finprov.loan.repository;

import com.finprov.loan.entity.Branch;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BranchRepository extends JpaRepository<Branch, Long> {
    Optional<Branch> findByCode(String code);

    @Query("SELECT b FROM Branch b WHERE b.deleted = false OR b.deleted IS NULL")
    List<Branch> findAllActive();

    List<Branch> findAllByDeletedFalse();
}
