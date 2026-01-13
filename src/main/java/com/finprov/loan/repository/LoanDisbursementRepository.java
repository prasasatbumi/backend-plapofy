package com.finprov.loan.repository;

import com.finprov.loan.entity.Loan;
import com.finprov.loan.entity.LoanDisbursement;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanDisbursementRepository extends JpaRepository<LoanDisbursement, Long> {
  Optional<LoanDisbursement> findByLoan(Loan loan);
}
