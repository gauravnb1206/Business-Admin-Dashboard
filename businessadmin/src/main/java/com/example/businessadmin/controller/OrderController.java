package com.example.businessadmin.controller;

import com.example.businessadmin.dto.OrderRequest;
import com.example.businessadmin.dto.OrderStatusUpdateRequest;
import com.example.businessadmin.dto.OrderResponse;
import com.example.businessadmin.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        return ResponseEntity.ok(
                service.createOrder(
                        request.getCustomerId(),
                        request.getItems(),
                        request.getPaymentStatus()
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(service.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getOrderById(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id, @RequestBody OrderStatusUpdateRequest req) {
        return ResponseEntity.ok(service.updateOrderStatus(id, req.getStatus()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> updateOrder(
            @PathVariable Long id,
            @RequestBody OrderRequest request) {
        return ResponseEntity.ok(service.updateOrder(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id ){
        service.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
