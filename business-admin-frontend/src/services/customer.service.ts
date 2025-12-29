import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';

export interface Customer {
  id?: number;
  name: string;
  email: string;
  phone?: string;
  address?: string;
  active?: boolean;
  createdAt?: string;
}

interface PaginatedResponse<T> {
  content: T[];
  pageable?: any;
  totalPages?: number;
  totalElements?: number;
  last?: boolean;
  first?: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class CustomerService {
  private apiUrl = 'http://localhost:8080/api/customers';

  constructor(private http: HttpClient) {}

  // Fetch paginated customers and return only content
  getAll(): Observable<Customer[]> {
    return this.http.get<PaginatedResponse<Customer>>(this.apiUrl).pipe(
      map(res => res.content || [])
    );
  }

  create(customer: Customer) {
    return this.http.post<Customer>(this.apiUrl, customer);
  }

  update(customer: Customer) {
    if (!customer.id) throw new Error('Customer ID is required for update.');
    return this.http.put<Customer>(`${this.apiUrl}/${customer.id}`, customer);
  }

  delete(id: number) {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
