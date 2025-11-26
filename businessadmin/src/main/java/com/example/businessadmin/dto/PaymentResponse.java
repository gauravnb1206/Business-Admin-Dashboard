package com.example.businessadmin.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PaymentResponse {
    private Long id;
    private Long orderId;
    private Double amount;
    private String paymentMethod;
    private LocalDateTime paymentDate;
    private String customerName;
    private String paymentStatus;
}
