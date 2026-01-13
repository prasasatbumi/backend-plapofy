package com.finprov.loan.service.impl;

import com.finprov.loan.entity.Customer;
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
    Customer customer =
        customerRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + id));
    customerRepository.delete(customer);
  }
}
