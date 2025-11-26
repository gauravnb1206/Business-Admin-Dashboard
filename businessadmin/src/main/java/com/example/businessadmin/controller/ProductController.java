package com.example.businessadmin.controller;

import com.example.businessadmin.entity.Product;
import com.example.businessadmin.service.ProductService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private  final ProductService service;

    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody Product product){
        return  ResponseEntity.ok(service.addProduct(product));
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts(){
        return ResponseEntity.ok(service.getAllProduct());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById( @PathVariable Long id){
        return ResponseEntity.ok(service.getProductById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        Product updated = service.updateProduct(id, productDetails);
        return ResponseEntity.ok(updated);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id){
        service.DeleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
