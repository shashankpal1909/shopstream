package com.shashank.product_service.service;

import com.shashank.product_service.domain.Product;
import com.shashank.product_service.repo.ProductRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {
    public final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    public Product create(Product p) {
        return repo.save(p);
    }

    public Optional<Product> getById(UUID id) {
        return repo.findById(id);
    }

    public Page<Product> list(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        return repo.findAll(pageable);
    }

    public Page<Product> listByCategory(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repo.findByCategoryIgnoreCase(category, pageable);
    }

    public Product update(UUID id, Product updated) {
        return repo.findById(id).map(existing -> {
            existing.setName(updated.getName());
            existing.setDescription(updated.getDescription());
            existing.setPrice(updated.getPrice());
            existing.setStock(updated.getStock());
            existing.setCategory(updated.getCategory());
            return repo.save(existing);
        }).orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
    }

    public void delete(UUID id) {
        repo.deleteById(id);
    }
}
