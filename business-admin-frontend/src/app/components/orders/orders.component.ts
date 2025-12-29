import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { OrderService, OrderRequest, OrderResponse } from '../../../services/order.service';
import { CustomerService, Customer } from '../../../services/customer.service';
import { ProductService, Product } from '../../../services/product.service';
import { PaymentService, PaymentRequest, PaymentResponse } from '../../../services/payment.service';
import { InvoiceService } from '../../../services/invoice.service';
import Swal from 'sweetalert2';


@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './orders.html',
  styleUrls: ['./orders.css']
})
export class OrdersComponent implements OnInit {

  orders: OrderResponse[] = [];
  customers: Customer[] = [];
  products: Product[] = [];
  
  editMode: boolean = false;
  editingOrderId?:number;

  showForm = false;
  expandedIndex: number | null = null;
  dropdownIndex: number | null = null;
  newOrder: OrderRequest = { customerId: 0, paymentStatus: 'Unpaid', items: [] };
  newItemProductId: number = 0;
  newItemQty = 1;

  //Payment variables
  showPaymentForm = false;
  selectedOrder?: OrderResponse;
  remainingAmount = 0;
  payment: PaymentRequest = { orderId: 0, amount: 0, paymentMethod: 'Cash' };
  paymentError = '';
  orderPayments: PaymentResponse[] = [];
  editingPayment = false;
  editingPaymentId?: number;

  searchTerm: string = '';
  selectedStatus: string = '';
  
  selectedMonth: string = '';
  selectedYear: string = new Date().getFullYear().toString();
  filteredOrders: OrderResponse[] = [];

  


  constructor(
    private orderService: OrderService,
    private productService: ProductService,
    private customerService: CustomerService,
    private paymentService: PaymentService,
    private invoiceService: InvoiceService
  ) {}

  ngOnInit(): void {
    this.loadOrders();
    this.loadCustomers();
    this.loadProducts();
   
  }

  


  private normalizeArrayResponse<T>(resp: any): T[] {
    if (!resp) return [];
    return Array.isArray(resp) ? resp : (resp.content ?? []);
  }

  loadOrders(): void {
  this.orderService.getAllOrders().subscribe({
    next: (data: any) => {
      const orders = Array.isArray(data) ? data : (data?.content ?? []);

      // 🧩 Normalize items so they always contain a product object
      this.orders = orders.map((order: any) => ({
        ...order,
        items: (order.items || []).map((it: any) => {
          // Try to find the product from your products list
          const productObj = this.products.find(p => p.id === (it.product?.id ?? it.productId));

          return {
            product: productObj || { id: it.productId, name: 'Unknown Product', price: it.price ?? 0 },
            quantity: it.quantity ?? 0,
            price: it.price ?? productObj?.price ?? 0
          };
        })
      }));
      this.filteredOrders = [...this.orders];


      console.log("✅ Loaded Orders:", this.orders);
    },
    error: err => console.error('Error loading orders', err)
  });
}



  loadCustomers(): void {
    this.customerService.getAll().subscribe({
      next: res => this.customers = this.normalizeArrayResponse<Customer>(res),
      error: err => console.error('Error loading customers', err)
    });
  }

  loadProducts(): void {
    this.productService.getAll().subscribe({
      next: res => this.products = this.normalizeArrayResponse<Product>(res),
      error: err => console.error('Error loading products', err)
    });
  }

  getProductNameById(id?: number): string {
    if (!id) return 'Unknown Product';
    const product = this.products.find(p => p.id === id);
    return product ? product.name : 'Unknown Product';
  }

  getProductPriceById(id?: number): number {
    if (!id) return 0;
    const product = this.products.find(p => p.id === id);
    return product ? product.price : 0;
  }

  addItem(): void {
  if (!this.newItemProductId || this.newItemProductId === 0 || !this.newItemQty) {
    alert('Select a product and enter a valid quantity');
    return;
  }
  

  const selectedProduct = this.products.find(p => p.id === this.newItemProductId);
  if (!selectedProduct) {
    alert('Product not found!');
    return;
  }

  const existing = this.newOrder.items.find(i => i.product.id === selectedProduct.id);
  if (existing) {
    existing.quantity += this.newItemQty;
  } else {
    // ✅ store full product details (not just id)
    this.newOrder.items.push({
  product: selectedProduct,
  quantity: this.newItemQty
});
  }

  this.newItemProductId = 0;
  this.newItemQty = 1;
}


  removeItem(index: number) {
    this.newOrder.items.splice(index, 1);
  }

  editOrder(order: OrderResponse) {
  this.showForm = true;
  this.editMode = true;
  this.editingOrderId = order.id;

  // Pre-fill the form
  this.newOrder = {
    customerId: order.customer.id,
    items: order.items.map(it => ({
      product: { id: it.product.id },
      quantity: it.quantity
    })),
    paymentStatus: order.paymentStatus ?? 'unpaid',
    status: order.status ?? 'Pending'
  };

  setTimeout(() => {
    const formSection = document.querySelector('.order-form');
    if(formSection){
      formSection.scrollIntoView({behavior:'smooth', block: 'start'});
    }
  },100);
}
  

saveOrder(): void {
  if (!this.newOrder.customerId || this.newOrder.items.length === 0) {
    Swal.fire({
      icon: 'warning',
      title: 'Incomplete Order',
      text: 'Please select a customer and add at least one item before saving the order.',
      confirmButtonColor: '#3085d6'
    });
    return;
  }

  const cleanRequest = {
    customerId: this.newOrder.customerId,
    paymentStatus: this.newOrder.paymentStatus,
    items: this.newOrder.items.map(item => ({
      product: { id: item.product.id },
      quantity: item.quantity
    })),
    status: this.newOrder.status ?? 'Pending'
  };

  if (this.editMode && this.editingOrderId) {
      // 🔹 UPDATE EXISTING ORDER
      this.orderService.updateOrder(this.editingOrderId, cleanRequest).subscribe({
        next: () => {
          Swal.fire('Updated!', 'Order updated successfully.', 'success');
          this.loadOrders();
          this.cancelForm();
        },
        error: err => {
          console.error('Error updating order', err);
          Swal.fire('Error', 'Failed to update order.', 'error');
        }
      });
    } else {
  this.orderService.createOrder(cleanRequest).subscribe({
    next: () => {
      Swal.fire({
        icon: 'success',
        title: 'Order Created',
        text: 'The order has been created successfully!',
        showConfirmButton: false,
        timer: 1500
      });
      this.loadOrders();
      this.cancelForm();
    },
    error: err => {
      console.error('Error creating order', err);
      Swal.fire({
        icon: 'error',
        title: 'Order Failed',
        text: 'Failed to create order. Please check the console for details.',
        confirmButtonColor: '#d33'
      });
    }
  });
  }
}



  cancelForm() {
    this.showForm = false;
    this.editMode = false;
    this.newOrder = { customerId: 0, paymentStatus: 'Unpaid', items: [] };
    this.newItemProductId = 0;
    this.newItemQty = 1;
  }

  toggleForm() {
    this.showForm = !this.showForm;
  }

  toggleExpand(index: number) {
    this.expandedIndex = this.expandedIndex === index ? null : index;
  }

  toggleDropdown(index: number, event: Event): void {
  event.stopPropagation();
  this.dropdownIndex = this.dropdownIndex === index ? null : index;
}


  deleteOrder(id: number): void {
  Swal.fire({
    title: 'Are you sure?',
    text: 'This will permanently delete the order.',
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: 'Yes, delete it!',
  }).then(result => {
    if (result.isConfirmed) {
      this.orderService.deleteOrder(id).subscribe({
        next: () => {
          Swal.fire('Deleted!', 'Order deleted successfully', 'success');
          this.loadOrders();
        },
        error: err => {
          if (err.status === 500 && err.error?.message?.includes('payments')) {
            Swal.fire('Cannot Delete', 'This order has related payments. Delete payments first.', 'error');
          } else {
            Swal.fire('Error', 'Failed to delete order', 'error');
          }
        }
      });
    }
  });
}

//filter order by month
filterByMonth(): void {
  if (!this.selectedMonth) {
    this.filteredOrders = [...this.orders];
    return;
  }

  const monthIndex = Number(this.selectedMonth);

  this.filteredOrders = this.orders.filter(order => {
    if (!order.createdAt) return false;

    const date = new Date(order.createdAt);
    return (
      date.getMonth() === monthIndex &&
      date.getFullYear().toString() === this.selectedYear
    );
  });
}

resetMonthFilter(): void {
  this.selectedMonth = '';
  this.filteredOrders = [...this.orders];
}

  
markDelivered(order: OrderResponse) {
  // ✅ Step 1: Prevent double delivery
  if (order.status === 'Delivered' || order.status === 'Completed') {
    Swal.fire({
      icon: 'info',
      title: 'Already Delivered',
      text: `Order #${order.id} has already been marked as Delivered.`,
      timer: 2000,
      showConfirmButton: false
    });
    return; // stop execution
  }

  // ✅ Step 2: Confirm marking as delivered
  Swal.fire({
    title: 'Mark as Delivered?',
    text: `Are you sure you want to mark order #${order.id} as Delivered?`,
    icon: 'question',
    showCancelButton: true,
    confirmButtonText: 'Yes, Deliver it!',
    cancelButtonText: 'Cancel'
  }).then(result => {
    if (result.isConfirmed) {

      // ✅ First update to Delivered
      this.orderService.updateOrderStatus(order.id!, 'Delivered').subscribe({
        next: () => {
          // ✅ Then update to Completed
          this.orderService.updateOrderStatus(order.id!, 'Completed').subscribe({
            next: () => {
              this.loadOrders(); // refresh orders list
              Swal.fire({
                icon: 'success',
                title: 'Order Completed!',
                text: `Order #${order.id} has been marked as Delivered and Completed.`,
                timer: 2000,
                showConfirmButton: false
              });
            },
            error: err => {
              console.error('Error completing order', err);
              Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'Failed to mark order as Completed.'
              });
            }
          });
        },
        error: err => {
          console.error('Error marking delivered', err);
          Swal.fire({
            icon: 'error',
            title: 'Error',
            text: 'Failed to mark order as Delivered.'
          });
        }
      });
    }
  });
}


  markCompleted(order: OrderResponse) {
    this.orderService.updateOrderStatus(order.id!, 'Completed').subscribe({
      next: () => this.loadOrders(),
      error: err => console.error('Error completing order', err)
    });
  }

  

// 🧾 Manage Payments
  openManagePayment(order: OrderResponse): void {
    console.log('Opening Manage Payment for Orders', order.id);
    this.selectedOrder = order;
    this.paymentService.getPaymentsByOrder(order.id!).subscribe({
      next: (res: PaymentResponse[]) => (this.orderPayments = res),
      error: (err) => console.error(err),
    });
    this.remainingAmount = (order.totalAmount || 0) - (order.paidAmount || 0);
    this.payment = { orderId: order.id!, amount: 0, paymentMethod: 'Cash' };

    this.paymentService.getPaymentsByOrder(order.id!).subscribe({
      next:(res: PaymentResponse[]) => {
        this.orderPayments =res;
      },
      error: (err) => console.error(err),
      
    });
    this.showPaymentForm = true;
  }

  editPayment(p: PaymentResponse): void {
    this.payment = {
      orderId: this.selectedOrder!.id!,
      amount: p.amount,
      paymentMethod: p.paymentMethod,
    };
    this.editingPayment = true;
    this.editingPaymentId = p.id;
  }

  deletePayment(id: number): void {
    this.paymentService.deletePayment(id).subscribe({
      next: () => {
        Swal.fire('Deleted', 'Payment removed', 'success');
        this.openManagePayment(this.selectedOrder!);
        this.loadOrders();
      },
      error: () => Swal.fire('Error', 'Failed to delete payment', 'error'),
    });
  }

submitPayment(): void {
  if (!this.payment || !this.selectedOrder) return;

  const remaining =
    (this.selectedOrder.totalAmount || 0) -
    (this.selectedOrder.paidAmount || 0);

    // ✅ NEW: if already fully paid
  if (remaining <= 0 && !this.editingPayment) {
    Swal.fire({
      icon: 'info',
      title: 'Payment Already Completed',
      text: 'This order is already fully paid.',
      timer: 1800,
      showConfirmButton: false,
    }).then(() => {
      this.closePaymentForm(); // ✅ AUTO CLOSE MODAL
    });
    return;
  }
  // ✅ Validation: positive amount
  if (this.payment.amount <= 0) {
    Swal.fire('Invalid Amount', 'Please enter a valid amount.', 'warning');
    return;
  }

  // ✅ Validation: prevent overpayment (new payments only)
  if (this.payment.amount > remaining && !this.editingPayment) {
    Swal.fire(
      'Overpayment Not Allowed',
      `You can pay only ₹${remaining.toFixed(2)}`,
      'warning'
    );
    return;
  }

  const req: PaymentRequest = {
    orderId: this.payment.orderId,
    amount: this.payment.amount,
    paymentMethod: this.payment.paymentMethod,
  };

  const apiCall = this.editingPayment
    ? this.paymentService.updatePayment(this.editingPaymentId!, req)
    : this.paymentService.createPayment(req);

  apiCall.subscribe({
    next: () => {
      Swal.fire({
        icon: 'success',
        title: this.editingPayment ? 'Payment Updated' : 'Payment Added',
        timer: 1200,
        showConfirmButton: false,
      }).then(() => {
        // ✅ CLOSE PAYMENT MODAL
        this.closePaymentForm();

        // ✅ REFRESH ORDERS LIST (payment status updates)
        this.loadOrders();
      });
    },
    error: () => {
      Swal.fire('Payment Failed', 'Something went wrong.', 'error');
    },
  });
}


  closePaymentForm(): void {
  this.showPaymentForm = false;
  this.selectedOrder = undefined;
  this.orderPayments = [];
  this.payment = { orderId: 0, amount: 0, paymentMethod: 'Cash' };
  this.editingPayment = false;
  this.editingPaymentId = undefined;
  this.paymentError = '';
}


  generateInvoice(order: any): void {
    if(!order.id) {
      alert('Invalid order Id1');
      return;
    }

    this.invoiceService.generateInvoice({orderId: order.id}).subscribe({
      next: () => {
        alert( `Invoice for order #${order.id} generated successfully!`);
      },
      error: (err: any) => {
        console.error('Error generating invoice:', err);
        alert('Could not generate invoice.');
      }
    });
  }

  // ✅ Mobile expand state (orderId based)
expandedMobileOrders = new Set<number>();

toggleMobileExpand(orderId: number): void {
  if (this.expandedMobileOrders.has(orderId)) {
    this.expandedMobileOrders.delete(orderId);
  } else {
    this.expandedMobileOrders.add(orderId);
  }
}

isMobileExpanded(orderId: number): boolean {
  return this.expandedMobileOrders.has(orderId);
}

}

  

  



