package com.shashank.notification_service.events;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentFailedEvent {
    private UUID orderId;
    private UUID userId;
    private String reason;
    private long timestamp;
}
