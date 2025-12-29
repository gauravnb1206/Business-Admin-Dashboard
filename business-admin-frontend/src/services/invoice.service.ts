import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface InvoiceRequest {
  orderId: number;
}

export interface InvoiceResponse {
  id: number;
  invoiceNumber: string;
  amount: number;
  date: string;
  orderId: number;
}



@Injectable({
  providedIn: 'root'
})
export class InvoiceService {

  private baseUrl = 'http://localhost:8080/api/invoices';

  constructor(private http: HttpClient) {}

  // ✅ Generate invoice for a specific order
  generateInvoice(request: InvoiceRequest): Observable<InvoiceResponse> {
    return this.http.post<InvoiceResponse>(`${this.baseUrl}`, request);
  }

  // ✅ Get all invoices
   getAllInvoices(): Observable<InvoiceResponse[]> {
    return this.http.get<InvoiceResponse[]>(this.baseUrl);
  }

  // ✅ Download invoice PDF
  downloadInvoice(id: number): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/${id}/download`, { responseType: 'blob' });
  }

  // ✅ (Optional) Get invoices by month/year (for filtering later)
  getInvoicesByMonth(month: number, year: number): Observable<InvoiceResponse[]> {
    return this.http.get<InvoiceResponse[]>(`${this.baseUrl}?month=${month}&year=${year}`);
  }
}
