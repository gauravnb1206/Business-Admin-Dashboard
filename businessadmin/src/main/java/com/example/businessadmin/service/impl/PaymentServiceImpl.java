package com.example.businessadmin.service.impl;

import com.example.businessadmin.dto.PaymentRequest;
import com.example.businessadmin.dto.PaymentResponse;
import com.example.businessadmin.entity.Order;
import com.example.businessadmin.entity.Payment;
import com.example.businessadmin.repository.OrderRepository;
import com.example.businessadmin.repository.PaymentRepository;
import com.example.businessadmin.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepo;
    private final OrderRepository orderRepo;

    @Override
    public PaymentResponse createPayment(PaymentRequest request) {
        Order order = orderRepo.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + request.getOrderId()));

        // ✅ match renamed field 'amount'
        Payment payment = Payment.builder()
                .order(order)
                .amount(request.getAmount()) // you can rename request field too (optional)
                .paymentMethod(request.getPaymentMethod())
                .build();

        Payment saved = paymentRepo.save(payment);

        // ✅ recalc total paid for this order
        Double totalPaid = paymentRepo.sumPaidForOrder(order.getId());
        if (totalPaid == null) totalPaid = 0.0;
        if (order.getTotalAmount() == null) order.setTotalAmount(0.0);

        if (totalPaid >= order.getTotalAmount()) {
            order.setPaymentStatus("Paid");
        } else if (totalPaid > 0) {
            order.setPaymentStatus("Partially Paid");
        } else {
            order.setPaymentStatus("Unpaid");
        }

        order.setPaidAmount(totalPaid); // ✅ make sure order.paidAmount updates
        orderRepo.save(order);

        return toDto(saved);
    }

    @Override
    public List<PaymentResponse> getPaymentsByOrder(Long orderId) {
        return paymentRepo.findByOrderId(orderId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentResponse> getAllPayments() {
        return paymentRepo.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PaymentResponse getPaymentById(Long id) {
        return toDto(paymentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found")));
    }

    @Override
    public PaymentResponse updatePayment(Long id, PaymentRequest updatedPayment) {
        Payment existing = paymentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        existing.setAmount(updatedPayment.getAmount());
        existing.setPaymentMethod(updatedPayment.getPaymentMethod());
        existing.setPaymentDate(LocalDateTime.now());
        paymentRepo.save(existing);

        // 🔹 Recalculate totals
        Long orderId = existing.getOrder().getId();
        Double totalPaid = paymentRepo.sumPaidForOrder(orderId);

        Order order = existing.getOrder();
        order.setPaidAmount(totalPaid);
        order.setPaymentStatus(
                totalPaid.equals(order.getTotalAmount()) ? "Paid"
                        : totalPaid < order.getTotalAmount() ? "Pending" : "Overpaid"
        );
        orderRepo.save(order);

        return toDto(existing);
    }


    @Override
    public void deletePayment(Long id) {
        Payment payment = paymentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        Long orderId = payment.getOrder().getId();

        paymentRepo.delete(payment);

        // 🔹 Update totals after deletion
        Double totalPaid = paymentRepo.sumPaidForOrder(orderId);
        if (totalPaid == null) totalPaid = 0.0;

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setPaidAmount(totalPaid);
        order.setPaymentStatus(
                totalPaid.equals(order.getTotalAmount()) ? "Paid"
                        : totalPaid < order.getTotalAmount() ? "Pending" : "Overpaid"
        );
        orderRepo.save(order);
    }

    // ✅ Update to use renamed 'amount' field
    private PaymentResponse toDto(Payment p) {
        return PaymentResponse.builder()
                .id(p.getId())
                .orderId(p.getOrder().getId())
                .amount(p.getAmount()) // ✅ matches entity
                .paymentMethod(p.getPaymentMethod())
                .paymentDate(p.getPaymentDate())
                .paymentStatus(p.getOrder().getPaymentStatus())
                .customerName(p.getOrder().getCustomer().getName())
                .build();
    }
}

