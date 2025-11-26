package com.example.businessadmin.service.impl;

import com.example.businessadmin.dto.InvoiceRequest;
import com.example.businessadmin.dto.InvoiceResponse;
import com.example.businessadmin.entity.Invoice;
import com.example.businessadmin.entity.Order;
import com.example.businessadmin.repository.InvoiceRepository;
import com.example.businessadmin.repository.OrderRepository;
import com.example.businessadmin.repository.PaymentRepository;
import com.example.businessadmin.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepo;
    private final OrderRepository orderRepo;
    private final PaymentRepository paymentRepo;

    @Override
    public InvoiceResponse generateInvoice(InvoiceRequest request) {
        if (request.getOrderId() == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }

        // 1️⃣ Fetch the order
        Order order = orderRepo.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // 2️⃣ Calculate totals
        Double totalPaid = paymentRepo.sumPaidForOrder(order.getId());
        if (totalPaid == null) totalPaid = 0.0;
        Double pending = Math.max(0.0, order.getTotalAmount() - totalPaid);

        // 3️⃣ Check if an invoice already exists for this order
        Optional<Invoice> existingInvoiceOpt = invoiceRepo.findByOrderId(order.getId());

        Invoice invoice;
        boolean isUpdated = false;

        if (existingInvoiceOpt.isPresent()) {
            // 🔁 Update the existing invoice instead of inserting new one
            invoice = existingInvoiceOpt.get();
            isUpdated = true;
        } else {
            // 🆕 Create a new invoice
            invoice = new Invoice();
            invoice.setOrder(order);
        }

        // 4️⃣ Common fields (set or update)
        invoice.setInvoiceNumber("INV-" + order.getId() + "-" + System.currentTimeMillis());
        invoice.setGeneratedAt(LocalDateTime.now());
        invoice.setTotalAmount(order.getTotalAmount());
        invoice.setTotalPaid(totalPaid);
        invoice.setPendingAmount(pending);

        // 5️⃣ Save invoice
        Invoice saved = invoiceRepo.save(invoice);

        // 6️⃣ Return proper response
        InvoiceResponse response = toDto(saved);
        response.setMessage(isUpdated ? "Invoice updated successfully" : "Invoice created successfully");
        return response;
    }

    @Override
    public List<InvoiceResponse> getAllInvoices() {
        return invoiceRepo.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public InvoiceResponse getInvoiceById(Long id) {
        return toDto(invoiceRepo.findById(id).orElseThrow(() -> new RuntimeException("Invoice not found")));
    }

    @Override
    public List<InvoiceResponse> getInvoiceByMonth(int month, int year) {
        return invoiceRepo.findInvoicesByMonthAndYear(month, year)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private InvoiceResponse toDto(Invoice i) {
        return InvoiceResponse.builder()
                .id(i.getId())
                .invoiceNumber(i.getInvoiceNumber())
                .generatedAt(i.getGeneratedAt())
                .totalAmount(i.getTotalAmount())
                .totalPaid(i.getTotalPaid())
                .pendingAmount(i.getPendingAmount())
                .orderId(i.getOrder().getId())
                .customerName(i.getOrder().getCustomer().getName())
                .build();
    }

    public Invoice getEntityById(Long id) {
        return invoiceRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
    }

}
