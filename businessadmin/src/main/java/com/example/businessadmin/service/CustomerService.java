package com.example.businessadmin.service;

import com.example.businessadmin.dto.CustomerRequest;
import com.example.businessadmin.dto.CustomerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerService {
    CustomerResponse createCustomer(CustomerRequest req);
    Page<CustomerResponse> getAllCustomers(Pageable pageable);
    CustomerResponse getCustomerById(Long id);
    CustomerResponse updateCustomer(Long id, CustomerRequest req);
    void deleteCustomer(Long id);

}
