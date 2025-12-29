import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService, Product } from '../../../services/product.service';
import Swal from 'sweetalert2';
@Component({
  selector: 'app-product',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './products.html',
  styleUrls: ['./products.css']
})
export class ProductComponent implements OnInit {

  products: Product[] = [];
  newProduct: Product = { name: '', description: '', price: 0, size: '' };
  editingProduct: Product | null = null;

  currentPage = 0;
  pageSize = 10;
  totalPages = 0;
  loading = false;

  constructor(private productService: ProductService) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts() {
    this.loading = true;
    this.productService.getAll(this.currentPage, this.pageSize).subscribe({
      next: (data) => {
        this.products = data.content || data; // handles paged or list response
        this.totalPages = data.totalPages || 1;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading products:', err);
        this.loading = false;
      }
    });
  }

  



addProduct() {
// 🟡 Validation alert
if (!this.newProduct.name || !this.newProduct.price || !this.newProduct.size) {
Swal.fire({
icon: 'warning',
title: 'Missing Fields',
text: 'Please fill out Product Name, Price, and Size before adding.',
confirmButtonColor: '#f39c12'
});
return;
}

// 🟢 Add product if valid
this.productService.create(this.newProduct).subscribe({
next: () => {
Swal.fire({
icon: 'success',
title: 'Product Added!',
text: 'The product was successfully added.',
showConfirmButton: false,
timer: 2000
});
this.resetForm();
this.loadProducts();
},
error: (err) => {
console.error('Error adding product:', err);
Swal.fire({
icon: 'error',
title: 'Error!',
text: 'Failed to add the product. Please try again later.',
confirmButtonColor: '#d33'
});
}
});
}


  editProduct(p: Product) {
    this.editingProduct = p;
    this.newProduct = { ...p };
  }

  updateProduct() {
  if (!this.editingProduct) return;

  const updatedProduct = { 
    ...this.newProduct, 
    id: this.editingProduct.id // ✅ ensure id is preserved
  };

  this.productService.update(updatedProduct).subscribe({
    next: () => {
      alert('Product updated successfully');
      this.cancelEdit();
      this.loadProducts();
    },
    error: (err) => console.error('Error updating product:', err)
  });
}


  deleteProduct(p: Product) {
    if (!confirm(`Are you sure you want to delete ${p.name}?`)) return;
    this.productService.delete(p.id!).subscribe({
      next: () => {
        alert('Product deleted successfully');
        this.loadProducts();
      },
      error: (err) => console.error('Error deleting product:', err)
    });
  }

  cancelEdit() {
    this.editingProduct = null;
    this.resetForm();
  }

  resetForm() {
    this.newProduct = { name: '', description: '', price: 0, size: '' };
  }

  nextPage() {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadProducts();
    }
  }

  prevPage() {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadProducts();
    }
  }
}
