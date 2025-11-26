package com.example.businessadmin.repository;

import com.example.businessadmin.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    boolean existsByInvoiceNumber(String invoiceNumber);

    Optional<Invoice> findByOrderId(Long orderId);

    @Query("SELECT i FROM Invoice i WHERE MONTH(i.generatedAt) = :month AND YEAR(i.generatedAt) = :year")
    List<Invoice> findInvoicesByMonthAndYear(@Param("month") int month, @Param("year") int year);
}
