package com.example.businessadmin.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class InvoiceResponse {
    private Long id;
    private String invoiceNumber;
    private LocalDateTime generatedAt;
    private Double totalAmount;
    private Double totalPaid;
    private Double pendingAmount;
    private Long orderId;
    private String message;
    private String customerName;
}
