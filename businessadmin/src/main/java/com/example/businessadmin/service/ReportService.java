package com.example.businessadmin.service;

import com.example.businessadmin.dto.MonthlyReportDto;

public interface ReportService {
    MonthlyReportDto getmonthlyReport(int year, int month);

}
