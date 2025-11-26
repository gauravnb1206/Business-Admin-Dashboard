package com.example.businessadmin.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MonthlyReportDto {

    private int year;
    private int month;

    private Long totalOrders;
    private Long totalCustomers;

    private Double totalOrderAmount;
    private Double totalPayments;
    private Double pendingAmount;

    private Double cashAmount;
    private Double upiAmount;
    private Double cardAmount;

    private Double expenseAmount;
    private Double netProfit;

    private String mostSoldProduct;
}
