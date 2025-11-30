package com.shashank.order_service.events;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderPlacedEvent {
    private UUID orderId;
    private UUID userId;
    private List<OrderItem> items;
    private BigDecimal total;
    private long createdAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItem {
        private UUID productId;
        private String name;
        private int qty;
        private BigDecimal price;
    }
}
