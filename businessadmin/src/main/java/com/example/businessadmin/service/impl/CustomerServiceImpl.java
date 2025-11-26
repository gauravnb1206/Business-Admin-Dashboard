package com.example.businessadmin.service.impl;

import com.example.businessadmin.dto.CustomerRequest;
import com.example.businessadmin.dto.CustomerResponse;
import com.example.businessadmin.entity.Customer;
import com.example.businessadmin.exception.BadRequestException;
import com.example.businessadmin.exception.ResourceNotFoundException;
import com.example.businessadmin.mapper.CustomerMapper;
import com.example.businessadmin.repository.CustomerRepository;
import com.example.businessadmin.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository repo;

    @Override
    public CustomerResponse createCustomer(CustomerRequest req) {
        repo.findByEmail(req.getEmail()).ifPresent(c -> {
            throw new BadRequestException("Email already exists");
        });
        Customer saved = repo.save(CustomerMapper.toEntity(req));
        return CustomerMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerResponse> getAllCustomers(Pageable pageable) {
        Page<Customer> page = repo.findAllByActiveTrue(pageable);
        return page.map(CustomerMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(Long id) {
        Customer c = repo.findByIdAndActiveTrue(id).orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        return CustomerMapper.toDto(c);
    }

    @Override
    public CustomerResponse updateCustomer(Long id, CustomerRequest req) {
        Customer c = repo.findByIdAndActiveTrue(id).orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        // check email conflicts if changed
        if (!c.getEmail().equals(req.getEmail())) {
            repo.findByEmail(req.getEmail()).ifPresent(existing -> {
                throw new BadRequestException("Email already in use by another customer");
            });
            c.setEmail(req.getEmail());
        }
        c.setName(req.getName());
        c.setPhone(req.getPhone());
        c.setAddress(req.getAddress());
        Customer updated = repo.save(c);
        return CustomerMapper.toDto(updated);
    }

    @Override
    public void deleteCustomer(Long id) {
        Customer c = repo.findByIdAndActiveTrue(id).orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        c.setActive(false);
        repo.save(c);
    }
}

