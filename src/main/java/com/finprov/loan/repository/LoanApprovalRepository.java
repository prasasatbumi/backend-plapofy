package com.finprov.loan.repository;

import com.finprov.loan.entity.Loan;
import com.finprov.loan.entity.LoanApproval;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanApprovalRepository extends JpaRepository<LoanApproval, Long> {
  List<LoanApproval> findByLoan(Loan loan);
}
