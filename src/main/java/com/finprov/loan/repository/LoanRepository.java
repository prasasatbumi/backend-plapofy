package com.finprov.loan.repository;

import com.finprov.loan.entity.Loan;
import com.finprov.loan.entity.LoanStatus;
import com.finprov.loan.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
  List<Loan> findByApplicant(User applicant);

  List<Loan> findByCurrentStatus(LoanStatus status);

  List<Loan> findByBranchIdIn(List<Long> branchIds);

  List<Loan> findAllByBranchId(Long branchId);

  boolean existsByApplicantAndCurrentStatusIn(User applicant, List<LoanStatus> statuses);
}
