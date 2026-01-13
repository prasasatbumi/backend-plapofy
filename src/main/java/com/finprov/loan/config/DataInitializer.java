package com.finprov.loan.config;

import com.finprov.loan.entity.Role;
import com.finprov.loan.entity.User;
import com.finprov.loan.repository.RoleRepository;
import com.finprov.loan.repository.UserRepository;
import com.finprov.loan.entity.Loan;
import com.finprov.loan.entity.LoanStatus;
import com.finprov.loan.entity.Plafond;
import com.finprov.loan.repository.LoanRepository;
import com.finprov.loan.repository.PlafondRepository;
import com.finprov.loan.repository.PermissionRepository;
import com.finprov.loan.repository.RolePermissionRepository;
import com.finprov.loan.repository.LoanApprovalRepository;
import com.finprov.loan.repository.LoanDisbursementRepository;
import com.finprov.loan.repository.NotificationRepository;
import java.math.BigDecimal;
import java.time.Instant;
import com.finprov.loan.entity.Permission;
import com.finprov.loan.entity.RolePermission;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import com.finprov.loan.entity.ProductInterest;
import com.finprov.loan.repository.ProductInterestRepository;
import com.finprov.loan.entity.Branch;
import com.finprov.loan.repository.BranchRepository;
import java.util.List;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

  private final RoleRepository roleRepository;
  private final UserRepository userRepository;
  private final PlafondRepository plafondRepository;
  private final LoanRepository loanRepository;
  private final PermissionRepository permissionRepository;
  private final RolePermissionRepository rolePermissionRepository;
  private final PasswordEncoder passwordEncoder;
  private final ProductInterestRepository productInterestRepository;
  private final BranchRepository branchRepository;
  private final LoanApprovalRepository loanApprovalRepository;
  private final LoanDisbursementRepository loanDisbursementRepository;
  private final NotificationRepository notificationRepository;

  @Override
  public void run(String... args) throws Exception {
    log.info("Starting Data Initialization...");

    Role superAdmin = createRoleIfNotFound("SUPER_ADMIN");
    Role marketing = createRoleIfNotFound("MARKETING");
    Role branchManager = createRoleIfNotFound("BRANCH_MANAGER");
    Role backOffice = createRoleIfNotFound("BACK_OFFICE");
    Role nasabah = createRoleIfNotFound("NASABAH");

    // Permissions
    Permission reviewPermission = createPermissionIfNotFound("LOAN_REVIEW", "Review Loan Application");
    Permission approvePermission = createPermissionIfNotFound("LOAN_APPROVE", "Approve Loan Application");
    Permission disbursePermission = createPermissionIfNotFound("LOAN_DISBURSE", "Disburse Loan Application");

    // Assign Permissions to SUPER_ADMIN
    assignPermissionToRole(superAdmin, reviewPermission);
    assignPermissionToRole(superAdmin, approvePermission);
    assignPermissionToRole(superAdmin, disbursePermission);

    var existingAdmin = userRepository.findByUsername("superadmin");
    if (existingAdmin.isEmpty()) {
      try {
        User user = User.builder()
            .username("superadmin")
            .email("superadmin@finprov.com")
            .password(passwordEncoder.encode("admin123"))
            .isActive(true)
            .roles(new HashSet<>(Set.of(superAdmin)))
            .build();
        userRepository.save(user);
        log.info("User 'superadmin' created.");
      } catch (DataIntegrityViolationException e) {
        log.warn("User 'superadmin' already exists (likely soft-deleted). Skipping creation.");
      }
    } else {
      User user = existingAdmin.get();
      user.setPassword(passwordEncoder.encode("admin123"));
      userRepository.save(user);
      log.info("User 'superadmin' password reset.");
    }

    // Initialize Branches
    Branch ciputat = createBranchIfNotFound("CIP", "Ciputat", "Jl. Ciputat Raya No. 1");
    Branch bekasi = createBranchIfNotFound("BKS", "Bekasi", "Jl. Ahmad Yani No. 10");
    Branch pondokIndah = createBranchIfNotFound("PIN", "Pondok Indah", "Jl. Metro Pondok Indah No. 5");
    Branch pondokPinang = createBranchIfNotFound("PPG", "Pondok Pinang", "Jl. Ciledug Raya No. 15");
    log.info("Branches initialized: Ciputat, Bekasi, Pondok Indah, Pondok Pinang");

    // Create multiple Nasabah users
    User nasabah1 = createTestUserIfNotFound("nasabah", "nasabah@finprov.com", "password123", nasabah, null);
    User nasabah2 = createTestUserIfNotFound("nasabah_budi", "budi@gmail.com", "password123", nasabah, null);
    User nasabah3 = createTestUserIfNotFound("nasabah_ani", "ani@gmail.com", "password123", nasabah, null);
    User nasabah4 = createTestUserIfNotFound("nasabah_dewi", "dewi@gmail.com", "password123", nasabah, null);
    log.info("Created 4 nasabah users");

    // Create Marketing users - each assigned to specific branches
    createTestUserIfNotFound("marketing_ciputat", "marketing_ciputat@finprov.com", "password123", marketing,
        Set.of(ciputat));
    createTestUserIfNotFound("marketing_bekasi", "marketing_bekasi@finprov.com", "password123", marketing,
        Set.of(bekasi));
    createTestUserIfNotFound("marketing_pi", "marketing_pi@finprov.com", "password123", marketing, Set.of(pondokIndah));
    createTestUserIfNotFound("marketing_pp", "marketing_pp@finprov.com", "password123", marketing,
        Set.of(pondokPinang));
    // Multi-branch marketing
    createTestUserIfNotFound("marketing", "marketing@finprov.com", "password123", marketing, Set.of(ciputat, bekasi));
    log.info("Created 5 marketing users");

    // Create Branch Manager users - each assigned to specific branches
    createTestUserIfNotFound("bm_ciputat", "bm_ciputat@finprov.com", "password123", branchManager, Set.of(ciputat));
    createTestUserIfNotFound("bm_bekasi", "bm_bekasi@finprov.com", "password123", branchManager, Set.of(bekasi));
    createTestUserIfNotFound("bm_pi", "bm_pi@finprov.com", "password123", branchManager, Set.of(pondokIndah));
    createTestUserIfNotFound("bm_pp", "bm_pp@finprov.com", "password123", branchManager, Set.of(pondokPinang));
    // Multi-branch manager
    createTestUserIfNotFound("branch_manager", "branch_manager@finprov.com", "password123", branchManager,
        Set.of(ciputat, pondokIndah));
    log.info("Created 5 branch manager users");

    // Back Office (centralized - no branch needed)
    createTestUserIfNotFound("back_office", "back_office@finprov.com", "password123", backOffice, null);
    log.info("Created back office user");

    Branch[] branchArray = { ciputat, bekasi, pondokIndah, pondokPinang };
    User[] nasabahUsers = { nasabah1, nasabah2, nasabah3, nasabah4 };

    // Filter out null users
    java.util.List<User> validNasabahUsers = java.util.Arrays.stream(nasabahUsers)
        .filter(u -> u != null)
        .collect(java.util.stream.Collectors.toList());

    if (!validNasabahUsers.isEmpty()) {
      initPlafondsAndLoans(validNasabahUsers, branchArray);
    } else {
      log.warn("Skipping loan seeding because no nasabah users could be created.");
    }

    log.info("Data Initialization Completed.");
  }

  private void initPlafondsAndLoans(java.util.List<User> applicants, Branch[] branches) {
    // 0. Clean up existing data to avoid duplicates/conflicts
    loanApprovalRepository.deleteAll(); // Delete dependent approvals first
    loanDisbursementRepository.deleteAll(); // Delete dependent disbursements
    notificationRepository.deleteAll(); // Delete dependent notifications if any

    loanRepository.deleteAll();
    productInterestRepository.deleteAll();
    plafondRepository.deleteAll();
    log.info("Cleared existing Loans, ProductInterests, and Plafonds.");

    // 1. Create Plafonds (Tiers)
    // Starter: 0.5M - 1.5M
    Plafond starter = createPlafond("STARTER", "Starter",
        new BigDecimal("500000000"), new BigDecimal("1500000000"),
        "Entry-level option for quick cash needs.");

    // Advance: 1.5M - 5M
    Plafond advance = createPlafond("ADVANCE", "Advance",
        new BigDecimal("1500000000"), new BigDecimal("5000000000"),
        "Higher limits for established borrowers.");

    // Priority: 5M - 15M
    Plafond priority = createPlafond("PRIORITY", "Priority",
        new BigDecimal("5000000000"), new BigDecimal("15000000000"),
        "Exclusive rates for our verified priority customers.");

    // 2. Create Product Interests (Seeding Rates)
    seedInterests(starter, 1.20, 3.30, 6.30, 12.00);
    seedInterests(advance, 1.00, 2.80, 5.50, 10.80);
    seedInterests(priority, 0.80, 2.40, 4.80, 9.60);

    // 3. Create Dummy Loans - more loans spread across branches and users
    log.info("Seeding dummy loans...");
    Random random = new Random();
    Plafond[] plafonds = { starter, advance, priority };

    // Create 50 loans spread across users and branches for better chart data
    for (int i = 0; i < 50; i++) {
      User selectedApplicant = applicants.get(random.nextInt(applicants.size()));
      Plafond selectedPlafond = plafonds[random.nextInt(plafonds.length)];

      // Weighted status selection for realistic pipeline
      // DRAFT(10%), SUBMITTED(20%), REVIEWED(20%), APPROVED(20%), DISBURSED(20%),
      // REJECTED(10%)
      double statusRoll = random.nextDouble();
      LoanStatus selectedStatus;
      if (statusRoll < 0.1)
        selectedStatus = LoanStatus.DRAFT;
      else if (statusRoll < 0.3)
        selectedStatus = LoanStatus.SUBMITTED;
      else if (statusRoll < 0.5)
        selectedStatus = LoanStatus.REVIEWED;
      else if (statusRoll < 0.7)
        selectedStatus = LoanStatus.APPROVED;
      else if (statusRoll < 0.9)
        selectedStatus = LoanStatus.DISBURSED;
      else
        selectedStatus = LoanStatus.REJECTED;

      Branch selectedBranch = branches[random.nextInt(branches.length)];

      // Random amount between min and max
      BigDecimal min = selectedPlafond.getMinAmount();
      BigDecimal max = selectedPlafond.getMaxAmount();
      BigDecimal range = max.subtract(min);
      BigDecimal randAmount = min.add(new BigDecimal(random.nextDouble()).multiply(range));

      // Round to nearest million for cleanliness
      randAmount = randAmount.divideToIntegralValue(new BigDecimal("1000000")).multiply(new BigDecimal("1000000"));

      // Random Tenor
      Integer[] tenors = { 1, 3, 6, 12 };
      Integer selectedTenor = tenors[random.nextInt(tenors.length)];

      // Fetch updated interests from DB since Plafond object might be stale
      java.util.List<com.finprov.loan.entity.ProductInterest> interests = productInterestRepository
          .findByPlafond(selectedPlafond);
      BigDecimal rate = interests.stream()
          .filter(rateObj -> rateObj.getTenor().equals(selectedTenor))
          .findFirst()
          .map(com.finprov.loan.entity.ProductInterest::getInterestRate)
          .orElse(BigDecimal.ZERO);

      // Calculate Installment
      BigDecimal totalInterestVal = randAmount.multiply(rate).divide(BigDecimal.valueOf(100), 2,
          java.math.RoundingMode.HALF_UP);
      BigDecimal totalPay = randAmount.add(totalInterestVal);
      BigDecimal mInstallment = totalPay.divide(BigDecimal.valueOf(selectedTenor), 0, java.math.RoundingMode.HALF_UP);

      Loan loan = Loan.builder()
          .applicant(selectedApplicant)
          .plafond(selectedPlafond)
          .branch(selectedBranch)
          .amount(randAmount)
          .tenor(selectedTenor)
          .interestRate(rate)
          .monthlyInstallment(mInstallment)
          .currentStatus(selectedStatus)
          .createdAt(Instant.now().minusSeconds(random.nextInt(60 * 24 * 3600))) // Last 60 days
          .purpose("Business Expansion " + (i + 1))
          .businessType("Retail")
          .ktpImagePath("/assets/dummy-ktp.png") // Added dummy KTP image
          .build();
      loanRepository.save(loan);
    }
    log.info("Seeded 50 dummy loans across {} applicants and {} branches.", applicants.size(), branches.length);
  }

  private Plafond createPlafond(String code, String name, BigDecimal min, BigDecimal max, String desc) {
    Plafond p = Plafond.builder()
        .code(code)
        .name(name)
        .minAmount(min)
        .maxAmount(max)
        .description(desc)
        .build();
    return plafondRepository.save(p);
  }

  private void seedInterests(Plafond p, double r1, double r3, double r6, double r12) {
    saveInterest(p, 1, r1);
    saveInterest(p, 3, r3);
    saveInterest(p, 6, r6);
    saveInterest(p, 12, r12);
  }

  private void saveInterest(Plafond p, int tenor, double rate) {
    com.finprov.loan.entity.ProductInterest pi = com.finprov.loan.entity.ProductInterest.builder()
        .plafond(p)
        .tenor(tenor)
        .interestRate(BigDecimal.valueOf(rate))
        .build();
    productInterestRepository.save(pi);
  }

  private User createTestUserIfNotFound(String username, String email, String password, Role role,
      Set<Branch> branches) {
    Optional<User> userOptional = userRepository.findByUsername(username);
    if (userOptional.isPresent()) {
      // Update branches if provided
      User existingUser = userOptional.get();
      if (branches != null && !branches.isEmpty()) {
        existingUser.setBranches(new HashSet<>(branches));
        userRepository.save(existingUser);
        log.info("User '{}' branches updated.", username);
      }
      return existingUser;
    }
    User user = User.builder()
        .username(username)
        .email(email)
        .password(passwordEncoder.encode(password))
        .isActive(true)
        .roles(new HashSet<>(Set.of(role)))
        .build();
    user.setBranches(branches);
    try {
      User savedUser = userRepository.save(user);
      log.info("User '{}' created.", username);
      return savedUser;
    } catch (DataIntegrityViolationException e) {
      log.warn("User '{}' already exists (likely soft-deleted). Skipping creation.", username);
      return null;
    }
  }

  private Branch createBranchIfNotFound(String code, String name, String address) {
    Optional<Branch> branchOptional = branchRepository.findByCode(code);
    if (branchOptional.isPresent()) {
      return branchOptional.get();
    }
    Branch branch = Branch.builder()
        .code(code)
        .name(name)
        .address(address)
        .deleted(false)
        .build();
    return branchRepository.save(branch);
  }

  private Role createRoleIfNotFound(String name) {
    Optional<Role> roleOptional = roleRepository.findByName(name);
    if (roleOptional.isPresent()) {
      return roleOptional.get();
    }
    Role role = Role.builder().name(name).build();
    return roleRepository.save(role);
  }

  private Permission createPermissionIfNotFound(String code, String name) {
    Optional<Permission> permission = permissionRepository.findByCode(code);
    if (permission.isPresent()) {
      return permission.get();
    }
    Permission p = Permission.builder().code(code).name(name).build();
    return permissionRepository.save(p);
  }

  private void assignPermissionToRole(Role role, Permission permission) {
    Optional<RolePermission> rolePermission = rolePermissionRepository.findByRoleAndPermission(role, permission);
    if (rolePermission.isPresent()) {
      return;
    }
    RolePermission rp = RolePermission.builder().role(role).permission(permission).build();
    rolePermissionRepository.save(rp);
  }
}
