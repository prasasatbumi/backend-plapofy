package com.finprov.loan.service;

import com.finprov.loan.entity.Customer;
import java.util.List;

public interface CustomerService {
  Customer createCustomer(Customer customer);

  List<Customer> getAllCustomers();

  void deleteCustomer(Long id);
}
