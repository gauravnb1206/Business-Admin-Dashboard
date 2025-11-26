package com.example.businessadmin.util;

import com.example.businessadmin.entity.Invoice;
import com.example.businessadmin.entity.Order;
import com.example.businessadmin.entity.OrderItem;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;

import java.awt.*;
import com.lowagie.text.Image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.time.format.DateTimeFormatter;

public class PdfGenerator {

    public static byte[] generateInvoicePdf(Invoice invoice) {
        try {
            Document document = new Document(PageSize.A4, 40, 40, 60, 50);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, out);
            document.open();

            Order order = invoice.getOrder();
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm");

            // === Load Company Logo ===
            try {
                String logoPath = "src/main/resources/static/New Logo.png"; // Adjust path as per your setup
                File file = new File(logoPath);
                if (file.exists()) {
                    Image logo = Image.getInstance(file.getAbsolutePath());
                    logo.scaleAbsolute(80, 80);
                    logo.setAlignment(Image.ALIGN_LEFT);
                    document.add(logo);
                }
            } catch (Exception e) {
                System.out.println("Logo not found, skipping logo...");
            }

            // === Company Header ===
            Font companyFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Color.BLACK);
            Paragraph company = new Paragraph("Shree Chips", companyFont);
            company.setAlignment(Element.ALIGN_CENTER);
            document.add(company);

            Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY);
            Paragraph companyInfo = new Paragraph(
                    "Ramnagri, Sinnar, Maharashtra\nEmail: shrichips1232@gmail.com | Phone: +91 91560 27744",
                    infoFont);
            companyInfo.setAlignment(Element.ALIGN_CENTER);
            document.add(companyInfo);
            document.add(Chunk.NEWLINE);

            // === Invoice Header ===
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Paragraph header = new Paragraph("INVOICE", headerFont);
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);
            document.add(Chunk.NEWLINE);

            // === Customer & Invoice Details ===
            PdfPTable detailsTable = new PdfPTable(2);
            detailsTable.setWidthPercentage(100);
            detailsTable.setSpacingBefore(10f);
            detailsTable.setSpacingAfter(10f);
            detailsTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

            detailsTable.addCell(new Phrase("Customer Name:", labelFont));
            detailsTable.addCell(new Phrase(order.getCustomer().getName(), valueFont));

            detailsTable.addCell(new Phrase("Invoice No:", labelFont));
            detailsTable.addCell(new Phrase(invoice.getInvoiceNumber(), valueFont));

            detailsTable.addCell(new Phrase("Order ID:", labelFont));
            detailsTable.addCell(new Phrase(String.valueOf(order.getId()), valueFont));

            detailsTable.addCell(new Phrase("Generated At:", labelFont));
            detailsTable.addCell(new Phrase(fmt.format(invoice.getGeneratedAt()), valueFont));

            detailsTable.addCell(new Phrase("Payment Status:", labelFont));
            detailsTable.addCell(new Phrase(order.getPaymentStatus(), valueFont));

            document.add(detailsTable);
            document.add(Chunk.NEWLINE);

            // === Items Table ===
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{4f, 2f, 2f, 2f});
            addTableHeader(table, "Item", "Quantity", "Unit Price (₹)", "Total (₹)");

            for (OrderItem item : order.getItems()) {
                double total = item.getQuantity() * item.getProduct().getPrice();
                table.addCell(item.getProduct().getName());
                table.addCell(String.valueOf(item.getQuantity()));
                table.addCell(String.format("₹%.2f", item.getProduct().getPrice()));
                table.addCell(String.format("₹%.2f", total));
            }

            document.add(table);
            document.add(Chunk.NEWLINE);

            // === Summary Section ===
            PdfPTable summary = new PdfPTable(2);
            summary.setWidthPercentage(40);
            summary.setHorizontalAlignment(Element.ALIGN_RIGHT);

            addSummaryRow(summary, "Total Amount:", "₹" + invoice.getTotalAmount());
            addSummaryRow(summary, "Paid:", "₹" + invoice.getTotalPaid());
            addSummaryRow(summary, "Pending:", "₹" + invoice.getPendingAmount());

            document.add(summary);
            document.add(Chunk.NEWLINE);

            // === Footer / Thank You ===
            Font thankFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 12, Color.DARK_GRAY);
            Paragraph thankYou = new Paragraph("Thank you for your business!", thankFont);
            thankYou.setAlignment(Element.ALIGN_CENTER);
            document.add(thankYou);

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generating invoice PDF", e);
        }
    }

    // --- Helper Methods ---
    private static void addTableHeader(PdfPTable table, String... headers) {
        Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(Color.LIGHT_GRAY);
            cell.setPadding(5);
            table.addCell(cell);
        }
    }

    private static void addSummaryRow(PdfPTable table, String label, String value) {
        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));

        labelCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setBorder(Rectangle.NO_BORDER);

        labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }
}
