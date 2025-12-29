import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";

export interface PaymentRequest{
    orderId: number;
    amount: number;
    paymentMethod: string;
}

export interface PaymentResponse {
    id: number;
    orderId: number;
    amount: number;
    paymentMethod: string;
    paymentDate?: string;
    paymentStatus: string;
    customerName: string;
}

@Injectable({
    providedIn: 'root' 
})
export class PaymentService{
    private baseUrl = 'http://localhost:8080/api/payments';

    constructor(private http: HttpClient) {}

    //create payment 
    createPayment(request: PaymentRequest): Observable<PaymentResponse>{
        return this.http.post<PaymentResponse>(this.baseUrl, request);
    }

    //list Payments, optionally filtered by orderId
    list(orderId?: number): Observable<PaymentResponse[]> {
    let params: HttpParams = new HttpParams();
    if (orderId != null) params = params.set('orderId', orderId.toString());
    return this.http.get<PaymentResponse[]>(this.baseUrl, { params });
    }

    // get single payment by Id
    getById(id : number) {
        return this.http.get<PaymentResponse>(`${this.baseUrl}/${id}`);
    }

    getPaymentsByOrder(orderId: number): Observable<PaymentResponse[]> {
        return this.http.get<PaymentResponse[]>(`${this.baseUrl}/${orderId}`);
    }

    getAllPayments(): Observable<PaymentResponse[]> {
        return this.http.get<PaymentResponse[]>(`${this.baseUrl}`);
    }


    updatePayment(id: number, request: PaymentRequest): Observable<PaymentResponse> {
        return this.http.put<PaymentResponse>(`${this.baseUrl}/${id}`, request);
    }

    deletePayment(id: number): Observable<any> {
        return this.http.delete(`${this.baseUrl}/${id}`);
    }


}