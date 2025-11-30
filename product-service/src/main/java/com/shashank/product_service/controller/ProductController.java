package com.shashank.product_service.controller;

import com.shashank.product_service.domain.Product;
import com.shashank.product_service.dto.ProductDto;
import com.shashank.product_service.dto.ProductMapper;
import com.shashank.product_service.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService svc;

    public ProductController(ProductService svc) {
        this.svc = svc;
    }

    @PostMapping
    public ResponseEntity<ProductDto> create(@RequestBody ProductDto dto) {
        Product p = ProductMapper.fromDto(dto);
        Product created = svc.create(p);
        return ResponseEntity.created(URI.create("/api/products/" + created.getId()))
                .body(ProductMapper.toDto(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> get(@PathVariable UUID id) {
        return svc.getById(id).map(p -> ResponseEntity.ok(ProductMapper.toDto(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<?> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String q
    ) {
        Page<Product> p;
        if (category != null) p = svc.listByCategory(category, page, size);
        else if (q != null) p = svc.repo.findByNameContainsIgnoreCase(q, PageRequest.of(page, size));
        else p = svc.list(page, size, sortBy);

        var dtoPage = p.map(ProductMapper::toDto);
        return ResponseEntity.ok(dtoPage);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> update(@PathVariable UUID id, @RequestBody ProductDto dto) {
        Product updated = svc.update(id, ProductMapper.fromDto(dto));
        return ResponseEntity.ok(ProductMapper.toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        svc.delete(id);
        return ResponseEntity.noContent().build();
    }
}
