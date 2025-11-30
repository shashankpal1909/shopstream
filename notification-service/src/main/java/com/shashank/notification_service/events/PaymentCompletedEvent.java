package com.shashank.notification_service.events;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCompletedEvent {
    private UUID orderId;
    private UUID userId;
    private BigDecimal amount;
    private long timestamp;
}
