package com.shashank.order_service.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "processed_events", uniqueConstraints = @UniqueConstraint(columnNames = "event_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessedEvent {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "event_id", nullable = false, unique = true)
    private String eventId;

    private Instant processedAt;
}
