package com.shashank.payment_service.events;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentCompletedEvent {
    private UUID orderId;
    private UUID userId;
    private BigDecimal amount;
    private long timestamp;
}

