package com.finprov.loan.service.impl;

import com.finprov.loan.dto.SubmitLoanRequest;
import com.finprov.loan.dto.TransitionRequest;
import com.finprov.loan.entity.*;
import com.finprov.loan.repository.*;
import com.finprov.loan.service.LoanService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

  private final LoanRepository loanRepository;
  private final PlafondRepository plafondRepository;
  private final UserRepository userRepository;
  private final LoanApprovalRepository loanApprovalRepository;
  private final LoanDisbursementRepository loanDisbursementRepository;
  private final NotificationRepository notificationRepository;
  private final com.finprov.loan.service.FileStorageService fileStorageService;
  private final com.finprov.loan.repository.BranchRepository branchRepository;

  @Override
  public Loan submitLoan(SubmitLoanRequest request) {
    User actor = currentUser();
    ensureRole(actor, "NASABAH");
    Plafond plafond = plafondRepository
        .findById(request.getPlafondId())
        .orElseThrow(() -> new IllegalArgumentException("Plafond not found"));

    // Validate branch
    if (request.getBranchId() == null) {
      throw new IllegalArgumentException("Branch is required");
    }
    com.finprov.loan.entity.Branch branch = branchRepository
        .findById(request.getBranchId())
        .orElseThrow(() -> new IllegalArgumentException("Branch not found"));

    if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Amount must be positive");
    }
    if (request.getAmount().compareTo(plafond.getMaxAmount()) > 0) {
      throw new IllegalArgumentException("Amount exceeds plafond limit");
    }
    if (request.getTenor() == null) {
      throw new IllegalArgumentException("Tenor is required");
    }

    // Find applicable interest rate
    ProductInterest interest = plafond.getInterests().stream()
        .filter(i -> i.getTenor().equals(request.getTenor()))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Invalid tenor for this product"));

    BigDecimal rate = interest.getInterestRate();
    BigDecimal principal = request.getAmount();
    BigDecimal totalInterestPerTenor = principal.multiply(rate).divide(BigDecimal.valueOf(100), 2,
        java.math.RoundingMode.HALF_UP);

    BigDecimal totalPayment = principal.add(totalInterestPerTenor);
    BigDecimal installment = totalPayment.divide(BigDecimal.valueOf(request.getTenor()), 0,
        java.math.RoundingMode.HALF_UP);

    Loan loan = Loan.builder()
        .applicant(actor)
        .plafond(plafond)
        .branch(branch)
        .amount(request.getAmount())
        .tenor(request.getTenor())
        .interestRate(rate)
        .monthlyInstallment(installment)
        .currentStatus(LoanStatus.SUBMITTED)
        .createdAt(Instant.now())
        .build();
    Loan saved = loanRepository.save(loan);
    recordApproval(saved, actor, "NASABAH", LoanStatus.SUBMITTED, "Submitted");
    return saved;
  }

  @Override
  @org.springframework.transaction.annotation.Transactional
  public Loan submitLoanWithKyc(com.finprov.loan.dto.LoanSubmissionWithKycRequest request,
      org.springframework.web.multipart.MultipartFile ktp,
      org.springframework.web.multipart.MultipartFile selfie,
      org.springframework.web.multipart.MultipartFile npwp,
      org.springframework.web.multipart.MultipartFile license) {

    User actor = currentUser();
    ensureRole(actor, "NASABAH");

    // 1. File Uploads
    if (ktp == null || ktp.isEmpty())
      throw new IllegalArgumentException("KTP Image is required");
    if (selfie == null || selfie.isEmpty())
      throw new IllegalArgumentException("Selfie with KTP is required");

    String userIdStr = String.valueOf(actor.getId());
    String ktpPath = fileStorageService.storeFile(ktp, userIdStr, "KTP");
    String selfiePath = fileStorageService.storeFile(selfie, userIdStr, "SELFIE");
    String npwpPath = (npwp != null && !npwp.isEmpty()) ? fileStorageService.storeFile(npwp, userIdStr, "NPWP") : null;
    String licensePath = (license != null && !license.isEmpty())
        ? fileStorageService.storeFile(license, userIdStr, "LICENSE")
        : null;

    // 2. Loan Logic (Calculations)
    Plafond plafond = plafondRepository
        .findById(request.getRequestedPlafondId())
        .orElseThrow(() -> new IllegalArgumentException("Plafond not found"));

    // Validate branch
    if (request.getBranchId() == null) {
      throw new IllegalArgumentException("Branch is required");
    }
    com.finprov.loan.entity.Branch branch = branchRepository
        .findById(request.getBranchId())
        .orElseThrow(() -> new IllegalArgumentException("Branch not found"));

    if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Amount must be positive");
    }
    if (request.getAmount().compareTo(plafond.getMaxAmount()) > 0) {
      throw new IllegalArgumentException("Amount exceeds plafond limit");
    }

    ProductInterest interest = plafond.getInterests().stream()
        .filter(i -> i.getTenor().equals(request.getTenorMonth()))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Invalid tenor for this product: " + request.getTenorMonth()));

    BigDecimal rate = interest.getInterestRate();
    BigDecimal principal = request.getAmount();
    BigDecimal totalInterestPerTenor = principal.multiply(rate).divide(BigDecimal.valueOf(100), 2,
        java.math.RoundingMode.HALF_UP);
    BigDecimal totalPayment = principal.add(totalInterestPerTenor);
    BigDecimal installment = totalPayment.divide(BigDecimal.valueOf(request.getTenorMonth()), 0,
        java.math.RoundingMode.HALF_UP);

    // 3. Save Entity
    Loan loan = Loan.builder()
        .applicant(actor)
        .plafond(plafond)
        .branch(branch)
        .amount(request.getAmount())
        .tenor(request.getTenorMonth())
        .interestRate(rate)
        .monthlyInstallment(installment)
        .currentStatus(LoanStatus.SUBMITTED)
        .createdAt(Instant.now())
        // New KYC Fields
        .kycStatus(KycStatus.PENDING)
        .purpose(request.getPurpose())
        .businessType(request.getBusinessType())
        .ktpImagePath(ktpPath)
        .selfieImagePath(selfiePath)
        .npwpImagePath(npwpPath)
        .businessLicenseImagePath(licensePath)
        .build();

    Loan saved = loanRepository.save(loan);
    recordApproval(saved, actor, "NASABAH", LoanStatus.SUBMITTED, "Submitted with KYC");
    return saved;
  }

  @Override
  public com.finprov.loan.dto.LoanSimulationResponse simulateLoan(
      com.finprov.loan.dto.LoanSimulationRequest request) {

    Plafond plafond = plafondRepository
        .findById(request.getPlafondId())
        .orElseThrow(() -> new IllegalArgumentException("Plafond not found"));

    if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Amount must be positive");
    }
    if (request.getAmount().compareTo(plafond.getMinAmount()) < 0) {
      throw new IllegalArgumentException("Amount below minimum limit: " + plafond.getMinAmount());
    }
    if (request.getAmount().compareTo(plafond.getMaxAmount()) > 0) {
      throw new IllegalArgumentException("Amount exceeds maximum limit: " + plafond.getMaxAmount());
    }

    ProductInterest interest = plafond.getInterests().stream()
        .filter(i -> i.getTenor().equals(request.getTenorMonth()))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Invalid tenor for this product: " + request.getTenorMonth()));

    BigDecimal rate = interest.getInterestRate();
    BigDecimal principal = request.getAmount();
    BigDecimal totalInterest = principal.multiply(rate).divide(BigDecimal.valueOf(100), 2,
        java.math.RoundingMode.HALF_UP);
    BigDecimal totalPayment = principal.add(totalInterest);
    BigDecimal monthlyInstallment = totalPayment.divide(BigDecimal.valueOf(request.getTenorMonth()), 0,
        java.math.RoundingMode.HALF_UP);

    // Admin fee (1% of principal, min 50k, max 500k)
    BigDecimal adminFee = principal.multiply(BigDecimal.valueOf(0.01))
        .max(BigDecimal.valueOf(50000))
        .min(BigDecimal.valueOf(500000));

    BigDecimal netDisbursement = principal.subtract(adminFee);

    return com.finprov.loan.dto.LoanSimulationResponse.builder()
        .requestedAmount(principal)
        .tenorMonth(request.getTenorMonth())
        .interestRate(rate)
        .monthlyInstallment(monthlyInstallment)
        .totalInterest(totalInterest)
        .totalPayment(totalPayment)
        .adminFee(adminFee)
        .netDisbursement(netDisbursement)
        .plafondName(plafond.getName())
        .build();
  }

  @Override
  public Loan reviewLoan(Long id, TransitionRequest request) {
    User actor = currentUser();
    ensureRole(actor, "MARKETING");
    Loan loan = loanRepository
        .findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Loan not found"));
    if (loan.getCurrentStatus() != LoanStatus.SUBMITTED) {
      throw new IllegalArgumentException("Loan must be SUBMITTED to review");
    }
    // Branch authorization: Marketing can only review loans from their assigned
    // branches
    if (!canAccessBranch(actor, loan.getBranch())) {
      throw new IllegalArgumentException("You are not authorized to review loans from this branch");
    }
    loan.setCurrentStatus(LoanStatus.REVIEWED);
    Loan saved = loanRepository.save(loan);
    recordApproval(
        saved,
        actor,
        "MARKETING",
        LoanStatus.REVIEWED,
        request != null ? request.getRemarks() : null);
    return saved;
  }

  @Override
  public Loan approveLoan(Long id, TransitionRequest request) {
    User actor = currentUser();
    ensureRole(actor, "BRANCH_MANAGER");
    Loan loan = loanRepository
        .findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Loan not found"));
    if (loan.getCurrentStatus() != LoanStatus.REVIEWED) {
      throw new IllegalArgumentException("Loan must be REVIEWED to approve");
    }
    // Branch authorization: Branch Manager can only approve loans from their
    // assigned branches
    if (!canAccessBranch(actor, loan.getBranch())) {
      throw new IllegalArgumentException("You are not authorized to approve loans from this branch");
    }
    loan.setCurrentStatus(LoanStatus.APPROVED);
    Loan saved = loanRepository.save(loan);
    recordApproval(
        saved,
        actor,
        "BRANCH_MANAGER",
        LoanStatus.APPROVED,
        request != null ? request.getRemarks() : null);
    notify(saved.getApplicant(), "LOAN_APPROVED", "Loan " + saved.getId() + " approved");
    return saved;
  }

  @Override
  public Loan disburseLoan(Long id, TransitionRequest request) {
    User actor = currentUser();
    ensureRole(actor, "BACK_OFFICE");
    Loan loan = loanRepository
        .findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Loan not found"));
    if (loan.getCurrentStatus() != LoanStatus.APPROVED) {
      throw new IllegalArgumentException("Loan must be APPROVED to disburse");
    }
    Optional<LoanDisbursement> existing = loanDisbursementRepository.findByLoan(loan);
    if (existing.isPresent()) {
      throw new IllegalArgumentException("Loan already disbursed");
    }
    BigDecimal amount = request != null ? request.getAmount() : null;
    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
      amount = loan.getAmount();
    }
    LoanDisbursement disb = LoanDisbursement.builder()
        .loan(loan)
        .amount(amount)
        .actor(actor)
        .timestamp(Instant.now())
        .build();
    loanDisbursementRepository.save(disb);
    loan.setCurrentStatus(LoanStatus.DISBURSED);
    Loan saved = loanRepository.save(loan);
    recordApproval(
        saved,
        actor,
        "BACK_OFFICE",
        LoanStatus.DISBURSED,
        request != null ? request.getRemarks() : null);
    notify(saved.getApplicant(), "LOAN_DISBURSED", "Loan " + saved.getId() + " disbursed");
    return saved;
  }

  @Override
  public Loan rejectLoan(Long id, TransitionRequest request) {
    User actor = currentUser();
    Loan loan = loanRepository
        .findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Loan not found"));

    // Branch Manager can reject REVIEWED loans
    // Back Office can reject APPROVED loans
    boolean canReject = false;
    String role = "";

    if (hasRole(actor, "BRANCH_MANAGER") && loan.getCurrentStatus() == LoanStatus.REVIEWED) {
      canReject = true;
      role = "BRANCH_MANAGER";
    } else if (hasRole(actor, "BACK_OFFICE") && loan.getCurrentStatus() == LoanStatus.APPROVED) {
      canReject = true;
      role = "BACK_OFFICE";
    }

    if (!canReject) {
      throw new IllegalArgumentException("You are not authorized to reject this loan in its current state");
    }

    loan.setCurrentStatus(LoanStatus.REJECTED);
    Loan saved = loanRepository.save(loan);
    recordApproval(saved, actor, role, LoanStatus.REJECTED, request != null ? request.getRemarks() : "Rejected");
    notify(saved.getApplicant(), "LOAN_REJECTED", "Loan " + saved.getId() + " has been rejected");
    return saved;
  }

  @Override
  public List<Loan> listLoans(Long branchId) {
    User actor = currentUser();
    if (hasRole(actor, "NASABAH")) {
      return loanRepository.findByApplicant(actor);
    }

    // Authorization for Branch Filter
    if (branchId != null) {
      // 1. Validate Branch Exists (Optional, but good for error 'branch not found')
      // For now, trust the ID or let query return empty.

      // 2. Check Access
      if (hasRole(actor, "SUPER_ADMIN") || hasRole(actor, "BACK_OFFICE")) {
        // Full access, just filter
        return loanRepository.findAllByBranchId(branchId);
      }

      // Marketing / Branch Manager: Must be assigned to this branch
      if (canAccessBranch(actor, com.finprov.loan.entity.Branch.builder().id(branchId).build())) {
        return loanRepository.findAllByBranchId(branchId);
      } else {
        throw new IllegalArgumentException("You are not authorized to view loans from this branch");
      }
    }

    // No Filter: Standard Logic
    // Back Office sees all loans (centralized)
    if (hasRole(actor, "BACK_OFFICE") || hasRole(actor, "SUPER_ADMIN")) {
      return loanRepository.findAll();
    }
    // Marketing and Branch Manager only see loans from their assigned branches
    java.util.Set<com.finprov.loan.entity.Branch> userBranches = actor.getBranches();
    if (userBranches == null || userBranches.isEmpty()) {
      return java.util.List.of(); // No branches assigned, return empty
    }
    java.util.List<Long> branchIds = userBranches.stream().map(com.finprov.loan.entity.Branch::getId).toList();
    return loanRepository.findByBranchIdIn(branchIds);
  }

  @Override
  public List<Loan> listOwnLoans() {
    User actor = currentUser();
    return loanRepository.findByApplicant(actor);
  }

  @Override
  public Loan getLoan(Long id) {
    Loan loan = loanRepository
        .findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Loan not found"));
    User actor = currentUser();
    if (hasRole(actor, "NASABAH") && !loan.getApplicant().getId().equals(actor.getId())) {
      throw new IllegalArgumentException("Not authorized to view this loan");
    }
    return loan;
  }

  private void recordApproval(
      Loan loan, User actor, String role, LoanStatus status, String remarks) {
    LoanApproval log = LoanApproval.builder()
        .loan(loan)
        .statusAfter(status)
        .actor(actor)
        .role(role)
        .remarks(remarks)
        .timestamp(Instant.now())
        .build();
    loanApprovalRepository.save(log);
  }

  private void notify(User user, String type, String message) {
    Notification notif = Notification.builder()
        .user(user)
        .type(type)
        .message(message)
        .createdAt(Instant.now())
        .read(false)
        .build();
    notificationRepository.save(notif);
  }

  private User currentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = auth.getName();
    return userRepository
        .findByUsername(username)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));
  }

  private void ensureRole(User user, String requiredRole) {
    if (!hasRole(user, requiredRole)) {
      throw new IllegalArgumentException("Forbidden: requires role " + requiredRole);
    }
  }

  private boolean hasRole(User user, String role) {
    return user.getRoles().stream().anyMatch(r -> role.equalsIgnoreCase(r.getName()));
  }

  private boolean canAccessBranch(User user, com.finprov.loan.entity.Branch branch) {
    if (user.getBranches() == null || user.getBranches().isEmpty()) {
      return false;
    }
    return user.getBranches().stream().anyMatch(b -> b.getId().equals(branch.getId()));
  }
}
