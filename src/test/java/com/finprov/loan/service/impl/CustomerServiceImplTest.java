package com.finprov.loan.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.finprov.loan.entity.Customer;
import com.finprov.loan.repository.CustomerRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

  @Mock private CustomerRepository customerRepository;

  @InjectMocks private CustomerServiceImpl customerService;

  private Customer customer;

  @BeforeEach
  void setUp() {
    customer =
        Customer.builder()
            .id(1L)
            .firstName("John")
            .lastName("Doe")
            .nik("1234567890123456")
            .phoneNumber("081234567890")
            .deleted(false)
            .build();
  }

  @Test
  void testCreateCustomer() {
    when(customerRepository.save(any(Customer.class))).thenReturn(customer);

    Customer created = customerService.createCustomer(customer);

    assertNotNull(created);
    assertEquals("John", created.getFirstName());
    verify(customerRepository, times(1)).save(any(Customer.class));
  }

  @Test
  void testGetAllCustomers() {
    when(customerRepository.findAll()).thenReturn(Arrays.asList(customer));

    List<Customer> customers = customerService.getAllCustomers();

    assertNotNull(customers);
    assertEquals(1, customers.size());
    assertEquals("John", customers.get(0).getFirstName());
    verify(customerRepository, times(1)).findAll();
  }

  @Test
  void testDeleteCustomer() {
    when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
    doNothing().when(customerRepository).delete(customer);

    customerService.deleteCustomer(1L);

    verify(customerRepository, times(1)).findById(1L);
    verify(customerRepository, times(1)).delete(customer);
  }
}
