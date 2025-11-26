package com.example.businessadmin.service.impl;

import com.example.businessadmin.dto.MonthlyReportDto;
import com.example.businessadmin.repository.CustomerRepository;
import com.example.businessadmin.repository.OrderRepository;
import com.example.businessadmin.repository.PaymentRepository;
import com.example.businessadmin.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final OrderRepository orderRepo;
    private final PaymentRepository paymentRepo;
    private final CustomerRepository customerRepo;

    @Override
    public MonthlyReportDto getmonthlyReport(int year, int month) {
        LocalDateTime start = LocalDate.of(year, month, 1).atStartOfDay();
        LocalDateTime end = start.plusMonths(1).minusNanos(1);

        // ✅ Core financial data
        Double totalOrderAmount = orderRepo.sumOrderAmountBetween(start, end);
        Double totalPayments = paymentRepo.sumPaymentsBetween(start, end);
        Long totalCustomers = orderRepo.countDistinctCustomersBetween(start, end);
        Long totalOrders = orderRepo.countOrdersBetween(start, end);

        // ✅ Payment mode breakdown
        Double cashAmount = paymentRepo.sumCashPaymentsBetween(start, end);
        Double upiAmount = paymentRepo.sumUpiPaymentsBetween(start, end);
        Double cardAmount = paymentRepo.sumCardPaymentsBetween(start, end);

        // Handle nulls safely
        totalOrderAmount = (totalOrderAmount == null) ? 0.0 : totalOrderAmount;
        totalPayments = (totalPayments == null) ? 0.0 : totalPayments;
        totalCustomers = (totalCustomers == null) ? 0L : totalCustomers;
        totalOrders = (totalOrders == null) ? 0L : totalOrders;
        cashAmount = (cashAmount == null) ? 0.0 : cashAmount;
        upiAmount = (upiAmount == null) ? 0.0 : upiAmount;
        cardAmount = (cardAmount == null) ? 0.0 : cardAmount;

        // ✅ Pending & profit calculations
        double pending = Math.max(totalOrderAmount - totalPayments, 0.0);

        // Optionally, if you add business expense tracking:
        double expenseAmount = 0.0; // later you can calculate it via ExpenseRepository
        double netProfit = totalPayments - expenseAmount;

        return MonthlyReportDto.builder()
                .year(year)
                .month(month)
                .totalOrders(totalOrders)
                .totalCustomers(totalCustomers)
                .totalOrderAmount(totalOrderAmount)
                .totalPayments(totalPayments)
                .pendingAmount(pending)
                .cashAmount(cashAmount)
                .upiAmount(upiAmount)
                .cardAmount(cardAmount)
                .expenseAmount(expenseAmount)
                .netProfit(netProfit)
                .build();
    }
}
