import { Component, OnInit } from '@angular/core';
import { ReportsService, MonthlyReport } from '../../../services/report.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-report',
  standalone: true,
  imports:[CommonModule],
  templateUrl: './reports.html',
  styleUrls: ['./reports.css']
})
export class ReportComponent implements OnInit {
  report: MonthlyReport | null = null;
  year: number = new Date().getFullYear();
  month: number = new Date().getMonth() + 1;
  loading = false;
  error = '';

  constructor(private reportService: ReportsService) {}

  ngOnInit(): void {
    this.loadReport();
  }

  loadReport(): void {
    this.loading = true;
    this.error = '';
    this.reportService.getMonthlyReport(this.year, this.month).subscribe({
      next: (data) => {
        this.report = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load report';
        this.loading = false;
      }
    });
  }

  changeMonth(offset: number): void {
    this.month += offset;
    if (this.month < 1) {
      this.month = 12;
      this.year -= 1;
    } else if (this.month > 12) {
      this.month = 1;
      this.year += 1;
    }
    this.loadReport();
  }

  getMonthName(): string {
    return new Date(this.year, this.month - 1).toLocaleString('default', { month: 'long' });
  }
}
