package com.example.businessadmin.mapper;

import com.example.businessadmin.dto.CustomerRequest;
import com.example.businessadmin.dto.CustomerResponse;
import com.example.businessadmin.entity.Customer;

public class CustomerMapper {
    public static Customer toEntity(CustomerRequest req) {
        return Customer.builder()
                .name(req.getName())
                .email(req.getEmail())
                .phone(req.getPhone())
                .address(req.getAddress())
                .active(true)
                .build();
    }

    public static CustomerResponse toDto(Customer c) {
        if (c == null) return null;
        return CustomerResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .email(c.getEmail())
                .phone(c.getPhone())
                .address(c.getAddress())
                .active(c.getActive())
                .createdAt(c.getCreatedAt())
                .build();
    }
}
