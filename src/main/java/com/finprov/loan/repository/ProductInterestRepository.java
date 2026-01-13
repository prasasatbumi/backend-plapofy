package com.finprov.loan.repository;

import com.finprov.loan.entity.ProductInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductInterestRepository extends JpaRepository<ProductInterest, Long> {
    java.util.List<ProductInterest> findByPlafond(com.finprov.loan.entity.Plafond plafond);
}
