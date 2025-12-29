package com.example.businessadmin.service;

import com.example.businessadmin.dto.OrderRequest;
import com.example.businessadmin.dto.OrderResponse;
import com.example.businessadmin.entity.OrderItem;


import java.util.List;

public interface OrderService {
    OrderResponse createOrder(Long customerId, List<OrderItem> items);
    List<OrderResponse> getAllOrders();
    OrderResponse getOrderById(Long id);
    OrderResponse updateOrderStatus(Long orderId, String status);
    OrderResponse updateOrder(Long orderId, OrderRequest request);
    void deleteOrder(Long id);
}
