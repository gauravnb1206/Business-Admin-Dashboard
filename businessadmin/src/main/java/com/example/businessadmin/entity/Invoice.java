package com.example.businessadmin.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Each invoice belong to an order
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false, unique = true)
    private  String invoiceNumber;

    @Column(nullable = false)
    private LocalDateTime generatedAt;

    private Double totalAmount;

    private Double totalPaid;

    private Double pendingAmount;

    private  LocalDateTime paymentDate;

}
