package com.shashank.cart_service.service;

import com.shashank.cart_service.dto.CartDto;
import com.shashank.cart_service.dto.CartItemDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class CartService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final String CART_PREFIX = "cart:";

    public CartService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public CartDto getCart(UUID userId) {
        String key = CART_PREFIX + userId;
        List<CartItemDto> items = (List<CartItemDto>) redisTemplate.opsForValue().get(key);
        if (items == null) items = new ArrayList<>();
        return CartDto.builder().userId(userId).items(items).build();
    }

    public void saveCart(CartDto cart) {
        String key = CART_PREFIX + cart.getUserId();
        redisTemplate.opsForValue().set(key, cart.getItems(), 7, TimeUnit.DAYS);
    }

    public void addItem(UUID userId, CartItemDto item) {
        CartDto cart = getCart(userId);
        List<CartItemDto> items = cart.getItems();
        Optional<CartItemDto> existing = items.stream()
                .filter(i -> i.getProductId().equals(item.getProductId()))
                .findFirst();
        if (existing.isPresent()) {
            existing.get().setQty(existing.get().getQty() + item.getQty());
        } else {
            items.add(item);
        }
        saveCart(CartDto.builder().userId(userId).items(items).build());
    }

    public void removeItem(UUID userId, UUID productId) {
        CartDto cart = getCart(userId);
        cart.getItems().removeIf(i -> i.getProductId().equals(productId));
        saveCart(cart);
    }

    public void clearCart(UUID userId) {
        String key = CART_PREFIX + userId;
        redisTemplate.delete(key);
    }
}
