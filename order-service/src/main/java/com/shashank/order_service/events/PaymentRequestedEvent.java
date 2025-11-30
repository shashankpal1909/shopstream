package com.shashank.order_service.events;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequestedEvent {
    private UUID orderId;
    private UUID userId;
    private BigDecimal amount;
}
