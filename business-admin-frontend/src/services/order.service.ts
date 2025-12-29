import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Product } from './product.service';
import { OrderItem } from './orderItem.service';



export interface OrderItemResponse {
  id?: number;
  productResponse: { id: number; name?: string; price?: number };
  quantity: number;
  price?: number;
}


// ✅ Customer model matches backend
export interface Customer {
  id: number;
  name: string;
  email?: string;
  phone?: string;
}



// ✅ Full OrderResponse with both dates included
export interface OrderResponse {
  id?: number;
  customer: Customer;
  totalAmount: number;
  paidAmount?: number;
  status: string;
  paymentStatus: string;
  createdAt?: string;   // for backend returning createdAt
  orderDate?: string;   // for backend returning orderDate
  items: OrderItem[];
}

export interface OrderItemRequest {
  product: Pick<Product, 'id'> | Product; // full product details
  quantity: number;
}

export interface OrderRequest {
  customerId: number;
  paymentStatus: string;
  items: OrderItemRequest[];
  status?: string;
}

@Injectable({
  providedIn: 'root',
})
export class OrderService {
  private baseUrl = 'http://localhost:8080/api/orders';

  constructor(private http: HttpClient) {}

  // ✅ Create order
  createOrder(order: OrderRequest): Observable<OrderResponse> {
    return this.http.post<OrderResponse>(this.baseUrl, order);
  }

  // ✅ Get all orders
  getAllOrders(): Observable<OrderResponse[]> {
    return this.http.get<OrderResponse[]>(this.baseUrl);
  }

  // ✅ Get order by ID
  getOrderById(id: number): Observable<OrderResponse> {
    return this.http.get<OrderResponse>(`${this.baseUrl}/${id}`);
  }

  // ✅ Update order status
  updateOrderStatus(id: number, status: string): Observable<OrderResponse> {
    return this.http.patch<OrderResponse>(`${this.baseUrl}/${id}/status`, { status });
  }

  // ✅ Update Order
  updateOrder(id: number, order:any): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/${id}`, order);
  }

  // ✅ Delete Order
  deleteOrder(id:number): Observable<any> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  
}
