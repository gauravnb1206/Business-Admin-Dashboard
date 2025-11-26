package com.example.businessadmin.controller;

import com.example.businessadmin.dto.PaymentRequest;
import com.example.businessadmin.dto.PaymentResponse;
import com.example.businessadmin.service.impl.PaymentServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentServiceImpl paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentRequest req){
        return ResponseEntity.ok(paymentService.createPayment(req));
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> listPayments(@RequestParam(required = false) Long orderId){
        if( orderId != null){
            return ResponseEntity.ok(paymentService.getPaymentsByOrder(orderId));
        }
        else{
            return ResponseEntity.ok(paymentService.getAllPayments());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable Long id){
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentResponse> updatePayment(
            @PathVariable Long id,
            @RequestBody PaymentRequest updatedPayment) {
        return ResponseEntity.ok(paymentService.updatePayment(id, updatedPayment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.ok("Payment deleted successfully");
    }
}
