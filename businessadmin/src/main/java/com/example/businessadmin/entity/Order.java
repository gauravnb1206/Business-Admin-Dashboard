package com.example.businessadmin.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Each order belong to one customer
    @ManyToOne
    @JoinColumn(name="Customer_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties("orders")
    private Customer customer;

    private LocalDateTime orderDate;
    private Double totalAmount;
    private Double paidAmount = 0.0;
    private String status;
    private String paymentStatus;

    @JsonManagedReference
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items;

    //For payments
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Payment> payments;

    private LocalDateTime createdAt = LocalDateTime.now();
}
