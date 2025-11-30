package com.shashank.cart_service.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemDto {
    private UUID productId;
    private String name;
    private int qty;
    private BigDecimal price;
}
