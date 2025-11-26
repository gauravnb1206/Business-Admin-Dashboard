package com.example.businessadmin.service.impl;

import com.example.businessadmin.entity.Product;
import com.example.businessadmin.repository.ProductRepository;
import com.example.businessadmin.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public List<Product> getAllProduct() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id" +id));
    }

    @Override
    public Product updateProduct(Long id, Product productDetails) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id " +id));

        existing.setName(productDetails.getName());
        existing.setPrice(productDetails.getPrice());
        existing.setSize(productDetails.getSize());
        existing.setDescription(productDetails.getDescription());

        return productRepository.save(existing);
    }

    @Override
    public void DeleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
