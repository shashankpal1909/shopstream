package com.shashank.payment_service.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue
    private UUID id;

    private UUID orderId;
    private UUID userId;
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
    }
}
