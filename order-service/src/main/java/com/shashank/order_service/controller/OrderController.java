package com.shashank.order_service.controller;

import com.shashank.order_service.domain.Order;
import com.shashank.order_service.repo.OrderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderRepository repo;

    public OrderController(OrderRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> get(@PathVariable UUID id) {
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> listForUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(repo.findAll().stream().filter(o -> o.getUserId().equals(userId)));
    }
}
