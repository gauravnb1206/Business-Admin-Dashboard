package com.example.businessadmin.service;

import com.example.businessadmin.entity.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public interface ProductService {
    Product addProduct(Product product);
    List<Product> getAllProduct();
    Product getProductById(Long id);
    Product updateProduct(Long id, Product productDetails);
    void DeleteProduct(Long id);
}
