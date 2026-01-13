package com.finprov.loan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class LoanManagementApplication {

  public static void main(String[] args) {
    SpringApplication.run(LoanManagementApplication.class, args);
  }
}
