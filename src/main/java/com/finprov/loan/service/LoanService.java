package com.finprov.loan.service;

import com.finprov.loan.dto.LoanSimulationRequest;
import com.finprov.loan.dto.LoanSimulationResponse;
import com.finprov.loan.dto.LoanSubmissionWithKycRequest;
import com.finprov.loan.dto.SubmitLoanRequest;
import com.finprov.loan.dto.TransitionRequest;
import com.finprov.loan.entity.Loan;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface LoanService {
  Loan submitLoan(SubmitLoanRequest request);

  LoanSimulationResponse simulateLoan(LoanSimulationRequest request);

  Loan reviewLoan(Long id, TransitionRequest request);

  Loan approveLoan(Long id, TransitionRequest request);

  Loan disburseLoan(Long id, TransitionRequest request);

  Loan rejectLoan(Long id, TransitionRequest request);

  List<Loan> listLoans(Long branchId);

  List<Loan> listOwnLoans();

  Loan getLoan(Long id);
}
