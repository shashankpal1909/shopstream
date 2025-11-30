package com.shashank.cart_service.controller;

import com.shashank.cart_service.dto.*;
import com.shashank.cart_service.events.OrderPlacedEvent;
import com.shashank.cart_service.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String ORDER_TOPIC = "order.placed";

    public CartController(CartService cartService, KafkaTemplate<String, Object> kafkaTemplate) {
        this.cartService = cartService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/{userId}/items")
    public ResponseEntity<Void> addItem(@PathVariable UUID userId, @RequestBody CartItemDto item) {
        cartService.addItem(userId, item);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}/items/{productId}")
    public ResponseEntity<Void> removeItem(@PathVariable UUID userId, @PathVariable UUID productId) {
        cartService.removeItem(userId, productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<CartDto> getCart(@PathVariable UUID userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PostMapping("/{userId}/checkout")
    public ResponseEntity<Map<String, Object>> checkout(@PathVariable UUID userId) {
        CartDto cart = cartService.getCart(userId);
        if (cart.getItems().isEmpty())
            return ResponseEntity.badRequest().body(Map.of("error", "cart is empty"));

        UUID orderId = UUID.randomUUID();
        List<OrderPlacedEvent.OrderItem> items = cart.getItems().stream().map(i ->
                OrderPlacedEvent.OrderItem.builder()
                        .productId(i.getProductId())
                        .name(i.getName())
                        .qty(i.getQty())
                        .price(i.getPrice())
                        .build()
        ).collect(Collectors.toList());

        BigDecimal total = items.stream()
                .map(it -> it.getPrice().multiply(new BigDecimal(it.getQty())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        OrderPlacedEvent event = OrderPlacedEvent.builder()
                .orderId(orderId)
                .userId(userId)
                .items(items)
                .total(total)
                .createdAt(Instant.now().toEpochMilli())
                .build();

        // publish event
        kafkaTemplate.send(ORDER_TOPIC, orderId.toString(), event);

        // clear cart
        cartService.clearCart(userId);

        return ResponseEntity.ok(Map.of("orderId", orderId.toString()));
    }
}
