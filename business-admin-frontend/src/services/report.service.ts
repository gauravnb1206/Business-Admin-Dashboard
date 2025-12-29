import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface MonthlyReport {
  year: number;
  month: number;
  totalOrders: number;
  totalCustomers: number;
  totalOrderAmount: number;
  totalPayments: number;
  pendingAmount: number;
  cashAmount?: number;
  upiAmount?: number;
  cardAmount?: number;
  expenseAmount?: number;
  netProfit?: number;
}

@Injectable({
  providedIn: 'root'
})
export class ReportsService {
  private baseUrl = 'http://localhost:8080/api/reports';

  constructor(private http: HttpClient) {}

  getMonthlyReport(year: number, month: number): Observable<MonthlyReport> {
    return this.http.get<MonthlyReport>(`${this.baseUrl}/monthly?year=${year}&month=${month}`);
  }
}
