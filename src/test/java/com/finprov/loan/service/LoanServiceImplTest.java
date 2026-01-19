package com.finprov.loan.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.finprov.loan.dto.SubmitLoanRequest;
import com.finprov.loan.dto.TransitionRequest;
import com.finprov.loan.entity.*;
import com.finprov.loan.repository.*;
import com.finprov.loan.service.impl.LoanServiceImpl;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

public class LoanServiceImplTest {

  private LoanRepository loanRepository;
  private PlafondRepository plafondRepository;
  private UserRepository userRepository;
  private LoanApprovalRepository loanApprovalRepository;
  private LoanDisbursementRepository loanDisbursementRepository;
  private NotificationRepository notificationRepository;
  private FileStorageService fileStorageService; // Added FileStorageService
  private BranchRepository branchRepository; // Added BranchRepository
  private CustomerRepository customerRepository; // Added CustomerRepository
  private ApplicationEventPublisher eventPublisher;
  private LoanService loanService;
  private User nasabah;
  private Plafond plaf;

  @BeforeEach
  void setup() {
    loanRepository = mock(LoanRepository.class);
    plafondRepository = mock(PlafondRepository.class);
    userRepository = mock(UserRepository.class);
    loanApprovalRepository = mock(LoanApprovalRepository.class);
    loanDisbursementRepository = mock(LoanDisbursementRepository.class);
    notificationRepository = mock(NotificationRepository.class);
    fileStorageService = mock(FileStorageService.class); // Mocked FileStorageService
    branchRepository = mock(BranchRepository.class); // Mocked BranchRepository
    customerRepository = mock(CustomerRepository.class); // Mocked CustomerRepository
    eventPublisher = mock(ApplicationEventPublisher.class);
    loanService = new LoanServiceImpl(
        loanRepository,
        plafondRepository,
        userRepository,
        loanApprovalRepository,
        loanDisbursementRepository,
        notificationRepository,
        fileStorageService,
        branchRepository,
        customerRepository,
        eventPublisher);

    Role rNasabah = Role.builder().id(1L).name("NASABAH").build();
    nasabah = User.builder().id(10L).username("john").roles(Set.of(rNasabah)).isActive(true).build();
    plaf = Plafond.builder()
        .id(5L)
        .code("PLF1")
        .name("Small")
        .maxAmount(new BigDecimal("10000000"))
        .build();

    SecurityContextHolder.getContext()
        .setAuthentication(new TestingAuthenticationToken("john", null));
    when(userRepository.findByUsername("john")).thenReturn(Optional.of(nasabah));
  }

  @Test
  void submitLoan_shouldSetSubmittedAndCreateApprovalLog() {
    when(plafondRepository.findById(5L)).thenReturn(Optional.of(plaf));
    SubmitLoanRequest req = new SubmitLoanRequest();
    req.setPlafondId(5L);
    req.setAmount(new BigDecimal("5000000"));
    when(loanRepository.save(Mockito.any()))
        .thenAnswer(
            inv -> {
              Loan l = inv.getArgument(0);
              l.setId(100L);
              return l;
            });
    Loan loan = loanService.submitLoan(req);
    assertEquals(LoanStatus.SUBMITTED, loan.getCurrentStatus());
    assertEquals(nasabah.getId(), loan.getApplicant().getId());
    verify(loanApprovalRepository, times(1)).save(Mockito.any());
  }

  @Test
  void approveLoan_shouldFailIfNotReviewed() {
    Loan loan = Loan.builder()
        .id(200L)
        .currentStatus(LoanStatus.SUBMITTED)
        .applicant(nasabah)
        .plafond(plaf)
        .amount(new BigDecimal("1000000"))
        .createdAt(Instant.now())
        .build();
    when(loanRepository.findById(200L)).thenReturn(Optional.of(loan));
    assertThrows(
        IllegalArgumentException.class,
        () -> loanService.approveLoan(200L, new TransitionRequest()));
  }
}
