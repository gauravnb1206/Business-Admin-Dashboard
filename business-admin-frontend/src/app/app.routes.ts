import { Routes } from '@angular/router';

import { LoginComponent } from './components/auth/login/login';
import { CustomersComponent } from './components/customers/customers.component';
import { ProductComponent } from './components/products/products.component';
import { OrdersComponent } from './components/orders/orders.component';
import { PaymentComponent } from './components/payments/payments.component';
import { InvoicesComponent } from './components/invoices/invoices.component';
import { ReportComponent } from './components/reports/reports.component';

import { AuthGuard } from './components/auth/auth.guard.guard';

export const routes: Routes = [

  { path: 'login', component: LoginComponent },

  { path: 'customers', component: CustomersComponent, canActivate: [AuthGuard] },
  { path: 'products', component: ProductComponent, canActivate: [AuthGuard] },
  { path: 'orders', component: OrdersComponent, canActivate: [AuthGuard] },
  { path: 'payments', component: PaymentComponent, canActivate: [AuthGuard] },
  { path: 'invoices', component: InvoicesComponent, canActivate: [AuthGuard] },
  { path: 'reports', component: ReportComponent, canActivate: [AuthGuard] },

  { path: '', redirectTo: 'login', pathMatch: 'full' }
];
