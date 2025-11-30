package com.shashank.payment_service.events;

import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentFailedEvent {
    private UUID orderId;
    private UUID userId;
    private String reason;
    private long timestamp;
}

