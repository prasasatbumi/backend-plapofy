package com.finprov.loan.service;

import com.finprov.loan.entity.Customer;
import java.util.List;

public interface CustomerService {
  Customer createCustomer(Customer customer);

  List<Customer> getAllCustomers();

  void deleteCustomer(Long id);

  Customer submitKyc(
      org.springframework.web.multipart.MultipartFile ktp,
      org.springframework.web.multipart.MultipartFile selfie,
      org.springframework.web.multipart.MultipartFile npwp,
      org.springframework.web.multipart.MultipartFile license);

  Customer verifyKyc(Long id, boolean approve, String remarks);
}
