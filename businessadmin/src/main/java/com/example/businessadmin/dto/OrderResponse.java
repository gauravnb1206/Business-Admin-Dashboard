package com.example.businessadmin.dto;


import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private CustomerResponse customer;

    private Long id;
    private String status;
    private String paymentStatus;
    private CustomerResponse customerResponse;
    private List<OrderItemResponse> items;
    private Double totalAmount;
    private LocalDateTime createdAt;
    private Double paidAmount;

}
