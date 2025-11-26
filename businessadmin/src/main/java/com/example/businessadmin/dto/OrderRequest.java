package com.example.businessadmin.dto;

import com.example.businessadmin.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    private Long customerId;
    private String paymentStatus;
    private List<OrderItem>  items;
    private String status;
}
