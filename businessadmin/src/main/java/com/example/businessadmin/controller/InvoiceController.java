package com.example.businessadmin.controller;

import com.example.businessadmin.dto.InvoiceRequest;
import com.example.businessadmin.dto.InvoiceResponse;
import com.example.businessadmin.entity.Invoice;
import com.example.businessadmin.service.impl.InvoiceServiceImpl;
import com.example.businessadmin.util.PdfGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/invoices")
public class InvoiceController {
    private final InvoiceServiceImpl invoiceService;

    @PostMapping
    public ResponseEntity<InvoiceResponse> createInvoice(@RequestBody InvoiceRequest request) {
        InvoiceResponse response = invoiceService.generateInvoice(request);
        return ResponseEntity.ok(response);
    }


    @GetMapping
    public ResponseEntity<List<InvoiceResponse>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long id) {
        Invoice invoice = invoiceService.getEntityById(id); // new helper method returning entity
        byte[] pdfBytes = PdfGenerator.generateInvoicePdf(invoice);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=invoice-" + invoice.getInvoiceNumber() + ".pdf")
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/month/{month}/{year}")
    public  ResponseEntity<List<InvoiceResponse>> getInvoicesByMonth(@PathVariable int month,@PathVariable int year){
        return  ResponseEntity.ok(invoiceService.getInvoiceByMonth(month, year));
    }
    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponse> getInvoice(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }

}
