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
    public OrderResponse createOrder(Long customerId, List<OrderItem> items, String paymentStatus) {
        // Find customer
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));

        // Calculate total & attach products
        double total = 0.0;
        for (OrderItem item : items) {
            Long pid = item.getProduct().getId();
            Product product = productRepo.findById(pid)
                    .orElseThrow(() -> new RuntimeException("Product not found with ID: " + pid));

            item.setProduct(product);
            item.setPrice(product.getPrice());
            total += product.getPrice() * item.getQuantity();
        }

        // Build order
        Order order = new Order();
        order.setCustomer(customer);
        order.setItems(items);
        order.setPaymentStatus(paymentStatus);
        order.setStatus("Pending");
        order.setCreatedAt(LocalDateTime.now());
        order.setTotalAmount(total);

        // Link items back to order
        items.forEach(i -> i.setOrder(order));

        // Save order (this persists order and items)
        Order saved = orderRepo.save(order);

        // ---- NEW: create payment record if paymentStatus is "Paid" ----
        // (This ensures paidAmount reflects actual payments, so frontend shows correct remaining)
        if (paymentStatus != null && paymentStatus.equalsIgnoreCase("Paid")) {
            // create a payment record equal to the order total
            // Adjust method/metadata as you need (e.g., if you accept paymentMethod in request)
            Payment payment = new Payment();           // adjust constructor/fields to your Payment entity
            payment.setOrder(saved);
            payment.setAmount(total);
            payment.setPaymentMethod("Cash");          // or set a default or accept from request
            payment.setPaymentDate(LocalDateTime.now()); // if Payment has createdAt
            paymentRepo.save(payment);

            // update order's paidAmount (optional but helpful)
            saved.setPaidAmount(total);
            // optionally update order.status/paymentStatus if you want
            orderRepo.save(saved);
        }
        // --------------------------------------------------------------

        // Return response mapped from saved order (mapToResponse will also pick up payments via paymentRepo.sumPaidForOrder)
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

        // Build a map of existing items by product id for quick lookup
        Map<Long, OrderItem> existingByProduct = new HashMap<>();
        if (existing.getItems() != null) {
            for (OrderItem oi : existing.getItems()) {
                if (oi.getProduct() != null && oi.getProduct().getId() != null) {
                    existingByProduct.put(oi.getProduct().getId(), oi);
                }
            }
        } else {
            existing.setItems(new ArrayList<>());
        }

        // New list to hold final order items (will replace existing.getItems() content)
        List<OrderItem> finalItems = new ArrayList<>();

        double total = 0.0;

        // Process incoming items from request
        if (request.getItems() != null) {
            for (OrderItem incoming : request.getItems()) {
                if (incoming.getProduct() == null || incoming.getProduct().getId() == null) {
                    throw new RuntimeException("Product ID missing in request item");
                }

                Long pid = incoming.getProduct().getId();

                // Fetch product entity (fresh)
                Product product = productRepo.findById(pid)
                        .orElseThrow(() -> new RuntimeException("Product not found with ID: " + pid));

                // If there is an existing OrderItem for this product, update it
                OrderItem existingItem = existingByProduct.remove(pid); // remove so leftover items in existingByProduct are deleted
                if (existingItem != null) {
                    existingItem.setQuantity(incoming.getQuantity());
                    existingItem.setPrice(product.getPrice());
                    // Ensure order link is correct
                    existingItem.setOrder(existing);
                    finalItems.add(existingItem);
                } else {
                    // Create a new OrderItem for this product
                    OrderItem newItem = new OrderItem();
                    newItem.setProduct(product);
                    newItem.setQuantity(incoming.getQuantity());
                    newItem.setPrice(product.getPrice());
                    newItem.setOrder(existing);
                    finalItems.add(newItem);
                }

                total += product.getPrice() * incoming.getQuantity();
            }
        }

        // Any remaining items in existingByProduct were removed on the client — orphanRemoval will delete them when we set items to finalItems
        // Replace existing items with final list
        existing.getItems().clear();
        existing.getItems().addAll(finalItems);

        // Update rest of order fields
        existing.setCustomer(customer);
        existing.setStatus(request.getStatus());
        existing.setPaymentStatus(request.getPaymentStatus());
        existing.setTotalAmount(total);

        // Note: we don't touch paidAmount here. Payments are handled separately (recommended).
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
