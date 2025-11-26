package com.example.businessadmin.repository;

import com.example.businessadmin.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface OrderRepository extends JpaRepository<Order, Long> {


    @Query("SELECT COALESCE(SUM(o.totalAmount),0) FROM Order o WHERE o.createdAt BETWEEN :start AND :end")
    Double sumOrderAmountBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(DISTINCT o.customer.id) FROM Order o WHERE o.createdAt BETWEEN :start AND :end")
    Long countDistinctCustomersBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt BETWEEN :start AND :end")
    Long countOrdersBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE MONTH(o.orderDate) = :month AND YEAR(o.orderDate) = :year")
    Double getTotalOrderAmountForMonth(@Param("month") int month, @Param("year") int year);



}
