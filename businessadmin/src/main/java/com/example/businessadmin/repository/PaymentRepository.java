package com.example.businessadmin.repository;

import com.example.businessadmin.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByOrderId(Long orderId);

    @Query("SELECT COALESCE(SUM(p.amount),0) FROM Payment p WHERE p.order.id = :orderId")
    Double sumPaidForOrder(@Param("orderId") Long orderId);

    @Query("SELECT COALESCE(SUM(p.amount),0) FROM Payment p WHERE p.paymentDate BETWEEN :start AND :end")
    Double sumPaymentsBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE MONTH(p.paymentDate) = :month AND YEAR(p.paymentDate) = :year")
    Double getTotalPaymentsForMonth(@Param("month") int month, @Param("year") int year);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.paymentMethod = 'CASH' AND p.paymentDate BETWEEN :start AND :end")
    Double sumCashPaymentsBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.paymentMethod = 'UPI' AND p.paymentDate BETWEEN :start AND :end")
    Double sumUpiPaymentsBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.paymentMethod = 'CARD' AND p.paymentDate BETWEEN :start AND :end")
    Double sumCardPaymentsBetween(LocalDateTime start, LocalDateTime end);

    Optional<Payment> findTopByOrderIdOrderByPaymentDateDesc(Long orderId);

}
