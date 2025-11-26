package com.example.businessadmin.controller;

import com.example.businessadmin.dto.MonthlyReportDto;
import com.example.businessadmin.service.impl.ReportServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportServiceImpl reportService;

    @GetMapping("/monthly")
    public ResponseEntity<MonthlyReportDto> monthlyReport(@RequestParam int year, @RequestParam int month){
        return ResponseEntity.ok(reportService.getmonthlyReport(year, month));
    }
}
