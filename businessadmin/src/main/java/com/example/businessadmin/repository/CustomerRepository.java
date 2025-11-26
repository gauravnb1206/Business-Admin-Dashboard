package com.example.businessadmin.repository;


import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.businessadmin.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
	Page<Customer> findAllByActiveTrue(Pageable pageable);
	Optional<Customer> findByIdAndActiveTrue(Long id);
	Optional<Customer>findByEmail(String email);
}
