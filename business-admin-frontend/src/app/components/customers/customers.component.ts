import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CustomerService, Customer } from '../../../services/customer.service';
import Swal from 'sweetalert2';
@Component({
  selector: 'app-customers',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './customers.html',
  styleUrls: ['./customers.css']
})
export class CustomersComponent implements OnInit {
  customers: Customer[] = [];
  newCustomer: Customer = { name: '', email: '', phone: '', address: '' };
  editingCustomer: Customer | null = null;
  loading = false;

  // Pagination
  currentPage = 0;
  pageSize = 10;
  totalPages = 1;

  constructor(private customerService: CustomerService) {}

  ngOnInit() {
  const token = localStorage.getItem('AUTH_TOKEN');

  if (!token) {
    console.warn('No token found, skipping customers API call');
    return;
  }

  this.loadCustomers();
}


  loadCustomers() {
  this.loading = true;
  this.customerService.getAll().subscribe({
    next: (data) => {
      this.customers = data; // data is already Customer[]
      this.loading = false;
      // Since pagination is removed from service, reset totalPages if needed
      this.totalPages = 1;
      this.currentPage = 0;
    },
    error: (err) => {
      console.error('Error fetching customers', err);
      this.loading = false;
    }
  });
}


  

addCustomer() {
  // ⚠️ Validation Alert
  if (!this.newCustomer.name || !this.newCustomer.email) {
    Swal.fire({
      icon: 'warning',
      title: 'Missing Information',
      text: 'Please enter both Name and Email before adding the customer.',
      confirmButtonColor: '#f0ad4e'
    });
    return;
  }

  // 🟢 Success and 🔴 Error Handling
  this.customerService.create(this.newCustomer).subscribe({
    next: () => {
      Swal.fire({
        icon: 'success',
        title: 'Customer Added',
        text: `${this.newCustomer.name} has been added successfully!`,
        showConfirmButton: false,
        timer: 1500
      });
      this.loadCustomers();
      this.newCustomer = { name: '', email: '', phone: '', address: '' };
    },
    error: (err) => {
      console.error('Error creating customer', err);
      Swal.fire({
        icon: 'error',
        title: 'Failed to Add Customer',
        text: 'An error occurred while adding the customer. Please try again later.',
        confirmButtonColor: '#d33'
      });
    }
  });
}


  editCustomer(c: Customer) {
    this.editingCustomer = { ...c }; // clone object
    this.newCustomer = { ...c };
  }

  updateCustomer() {
    if (!this.editingCustomer) return;

    this.customerService.update(this.newCustomer).subscribe({
      next: (updated) => {
        alert('Customer updated successfully');
        this.editingCustomer = null;
        this.newCustomer = { name: '', email: '', phone: '', address: '' };
        this.loadCustomers();
      },
      error: (err) => console.error('Error updating customer', err)
    });
  }

  cancelEdit() {
    this.editingCustomer = null;
    this.newCustomer = { name: '', email: '', phone: '', address: '' };
  }

  deleteCustomer(c: Customer) {
    if (!confirm(`Are you sure to delete ${c.name}?`)) return;

    this.customerService.delete(c.id!).subscribe({
      next: () => {
        alert('Customer deleted successfully');
        this.loadCustomers();
      },
      error: (err) => console.error('Error deleting customer', err)
    });
  }

  prevPage() {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadCustomers();
    }
  }

  nextPage() {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadCustomers();
    }
  }
}
