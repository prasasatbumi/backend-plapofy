package com.finprov.loan.service.impl;

import com.finprov.loan.entity.Customer;
import com.finprov.loan.entity.User;
import com.finprov.loan.repository.CustomerRepository;
import com.finprov.loan.service.CustomerService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

  private final CustomerRepository customerRepository;
  private final com.finprov.loan.service.FileStorageService fileStorageService;
  private final com.finprov.loan.repository.UserRepository userRepository;
  private final com.finprov.loan.repository.NotificationRepository notificationRepository;

  @Override
  @CacheEvict(value = "customers", allEntries = true)
  public Customer createCustomer(Customer customer) {
    return customerRepository.save(customer);
  }

  @Override
  @Cacheable(value = "customers")
  public List<Customer> getAllCustomers() {
    log.info("Fetching customers from database...");
    return customerRepository.findAll();
  }

  @Override
  @CacheEvict(value = "customers", allEntries = true)
  public void deleteCustomer(Long id) {
    Customer customer = customerRepository
        .findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + id));
    customerRepository.delete(customer);
  }

  @Override
  @org.springframework.transaction.annotation.Transactional
  public Customer submitKyc(
      org.springframework.web.multipart.MultipartFile ktp,
      org.springframework.web.multipart.MultipartFile selfie,
      org.springframework.web.multipart.MultipartFile npwp,
      org.springframework.web.multipart.MultipartFile license) {

    User actor = currentUser();
    Customer customer = customerRepository.findByUserId(actor.getId())
        .orElseGet(() -> {
          // Auto-create customer profile if missing
          return Customer.builder()
              .user(actor)
              .firstName(actor.getUsername()) // Default placeholder
              .lastName("")
              .build();
        });

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

    customer.setKtpImagePath(ktpPath);
    customer.setSelfieImagePath(selfiePath);
    customer.setNpwpImagePath(npwpPath);
    customer.setBusinessLicenseImagePath(licensePath);
    customer.setKycStatus(com.finprov.loan.entity.KycStatus.PENDING);

    return customerRepository.save(customer);
  }

  @Override
  @org.springframework.transaction.annotation.Transactional
  public Customer verifyKyc(Long id, boolean approve, String remarks) {
    Customer customer = customerRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

    if (approve) {
      customer.setKycStatus(com.finprov.loan.entity.KycStatus.VERIFIED);
      notify(customer.getUser(), "KYC_APPROVED", "Your KYC has been approved. You can now apply for loans.");
    } else {
      customer.setKycStatus(com.finprov.loan.entity.KycStatus.REJECTED);
      notify(customer.getUser(), "KYC_REJECTED", "Your KYC was rejected: " + remarks);
    }
    return customerRepository.save(customer);
  }

  private User currentUser() {
    org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder
        .getContext().getAuthentication();
    String username = auth.getName();
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));
  }

  private void notify(User user, String type, String message) {
    com.finprov.loan.entity.Notification notif = com.finprov.loan.entity.Notification.builder()
        .user(user)
        .type(type)
        .message(message)
        .createdAt(java.time.Instant.now())
        .read(false)
        .build();
    notificationRepository.save(notif);
  }
}
