package com.example.businessadmin.service.impl;

import com.example.businessadmin.dto.*;
import com.example.businessadmin.entity.*;
import com.example.businessadmin.repository.*;
import com.example.businessadmin.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepo;
    private final CustomerRepository customerRepo;
    private final ProductRepository productRepo;
    private final PaymentRepository paymentRepo;

    @Override
    public OrderResponse createOrder(Long customerId, List<OrderItem> items) {

        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));

        double total = 0.0;
        for (OrderItem item : items) {
            Long pid = item.getProduct().getId();
            Product product = productRepo.findById(pid)
                    .orElseThrow(() -> new RuntimeException("Product not found with ID: " + pid));

            item.setProduct(product);
            item.setPrice(product.getPrice());
            total += product.getPrice() * item.getQuantity();
        }

        Order order = new Order();
        order.setCustomer(customer);
        order.setItems(items);
        order.setStatus("Pending");
        order.setPaymentStatus("Unpaid");   // ✅ always unpaid initially
        order.setPaidAmount(0.0);
        order.setTotalAmount(total);
        order.setCreatedAt(LocalDateTime.now());

        items.forEach(i -> i.setOrder(order));

        Order saved = orderRepo.save(order);
        return mapToResponse(saved);
    }



    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepo.findAll().stream().map(this::mapToResponse).toList();
    }

    @Override
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return mapToResponse(order);
    }

    @Override
    public OrderResponse updateOrderStatus(Long orderId, String status) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if ("Delivered".equals(order.getStatus()) && "Delivered".equals(status)) {
            throw new RuntimeException("Order is already delivered");
        }
        order.setStatus(status);

        Order updated = orderRepo.save(order);
        return mapToResponse(updated);
    }

    @Override
    public OrderResponse updateOrder(Long orderId, OrderRequest request) {

        Order existing = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

        Customer customer = customerRepo.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + request.getCustomerId()));

        // 🔹 Map existing items by productId
        Map<Long, OrderItem> existingByProduct = new HashMap<>();
        if (existing.getItems() != null) {
            for (OrderItem oi : existing.getItems()) {
                existingByProduct.put(oi.getProduct().getId(), oi);
            }
        }

        List<OrderItem> finalItems = new ArrayList<>();
        double total = 0.0;

        // 🔹 Process incoming items
        for (OrderItem incoming : request.getItems()) {
            Product product = productRepo.findById(incoming.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            OrderItem item = existingByProduct.remove(product.getId());

            if (item == null) {
                item = new OrderItem();
                item.setOrder(existing);
                item.setProduct(product);
            }

            item.setQuantity(incoming.getQuantity());
            item.setPrice(product.getPrice());

            total += product.getPrice() * incoming.getQuantity();
            finalItems.add(item);
        }

        // 🔹 Replace items (orphanRemoval will delete removed ones)
        existing.getItems().clear();
        existing.getItems().addAll(finalItems);

        // 🔹 Update ONLY order details
        existing.setCustomer(customer);
        existing.setStatus(request.getStatus());
        existing.setTotalAmount(total);

        // ❗❗ DO NOT TOUCH paymentStatus HERE ❗❗
        // ❗❗ DO NOT TOUCH paidAmount HERE ❗❗

        Order updated = orderRepo.save(existing);
        return mapToResponse(updated);
    }





    @Override
    public void deleteOrder(Long id) {
        if (!orderRepo.existsById(id)) {
            throw new RuntimeException("Order not found");
        }
        orderRepo.deleteById(id);
    }

    // ✅ Entity → DTO mapping
    private OrderResponse mapToResponse(Order order) {
        Double totalPaid = paymentRepo.sumPaidForOrder(order.getId());
        if (totalPaid == null) totalPaid = 0.0;

        CustomerResponse customerResponse = CustomerResponse.builder()
                .id(order.getCustomer().getId())
                .name(order.getCustomer().getName())
                .email(order.getCustomer().getEmail())
                .phone(order.getCustomer().getPhone())
                .build();

        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(i -> OrderItemResponse.builder()
                        .id(i.getId())
                        .product(ProductResponse.builder()
                                .id(i.getProduct().getId())
                                .name(i.getProduct().getName())
                                .price(i.getProduct().getPrice())
                                .build())
                        .quantity(i.getQuantity())
                        .price(i.getPrice())
                        .build())
                .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .status(order.getStatus())
                .paymentStatus(order.getPaymentStatus())
                .totalAmount(order.getTotalAmount())
                .paidAmount(totalPaid)
                .createdAt(order.getCreatedAt())
                .customer(customerResponse)
                .items(itemResponses)
                .build();
    }
}
