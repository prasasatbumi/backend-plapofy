package com.finprov.loan.repository;

import com.finprov.loan.entity.Plafond;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlafondRepository extends JpaRepository<Plafond, Long> {
    java.util.Optional<Plafond> findByCode(String code);

    java.util.List<Plafond> findAllByDeletedFalse();
}
