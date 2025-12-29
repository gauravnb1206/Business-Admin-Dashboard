import { PaymentService } from '../../../services/payment.service';
import { Component, OnInit } from "@angular/core";
import { PaymentResponse } from '../../../services/payment.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-payments',
  standalone:true,
  imports : [CommonModule, FormsModule],
  templateUrl:'./payments.html',
  styleUrls: ['./payments.css']
})

export class PaymentComponent implements OnInit{
  payments: PaymentResponse[] = [];
  filteredPayments: PaymentResponse[] = [];
  searchTerm = '';
  selectedStatus = 'All';
  loading = true;

  constructor(private paymentService: PaymentService){}

  ngOnInit(): void{
    this.loadPayments();
  }

  loadPayments(): void{
    this.paymentService.getAllPayments().subscribe({
      next:(res) => {
        this.payments = res;
        this.filteredPayments = res;
        this.loading =false;
      },
      error: (err) =>{
        console.error('Error loading payments', err);
        this.loading = false;
        
      },
    });
  }

  searchPayments(): void {
    const term = this.searchTerm.toLowerCase();
    this.filteredPayments = this.payments.filter(p => 
      p.customerName?.toLowerCase().includes(term) || 
      p.orderId?.toString().includes(term)
    );
  }

  filterByStatus(): void{
    if(this.selectedStatus === 'All'){
      this.filteredPayments = [...this.payments];

    }else {
      this.filteredPayments = this.payments.filter(
        p => p.paymentStatus === this.selectedStatus 
      );
    }

  }

}