package com.example.businessadmin.service;

import com.example.businessadmin.dto.InvoiceRequest;
import com.example.businessadmin.dto.InvoiceResponse;

import java.util.List;

public interface InvoiceService {
    InvoiceResponse generateInvoice(InvoiceRequest request);
    List<InvoiceResponse> getAllInvoices();
    InvoiceResponse getInvoiceById(Long id);
    List<InvoiceResponse> getInvoiceByMonth(int month, int year);
}
