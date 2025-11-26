package com.example.businessadmin.service;

import com.example.businessadmin.dto.PaymentRequest;
import com.example.businessadmin.dto.PaymentResponse;

import java.util.List;

public interface PaymentService {
    PaymentResponse createPayment(PaymentRequest request);
    List<PaymentResponse> getPaymentsByOrder(Long orderId);
    List<PaymentResponse> getAllPayments();
    PaymentResponse getPaymentById(Long id);
    PaymentResponse updatePayment(Long id, PaymentRequest updatedPayment);
    void deletePayment(Long id);

}
