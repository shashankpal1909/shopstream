package com.shashank.payment_service.listener;

import com.shashank.payment_service.domain.*;
import com.shashank.payment_service.events.*;
import com.shashank.payment_service.repo.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentRequestListener {

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "payment.requested", groupId = "payment-service-group", containerFactory = "kafkaListenerContainerFactory")
    @Transactional
    public void consume(PaymentRequestedEvent event) {
        log.info("Received payment.requested for order {} amount {}", event.getOrderId(), event.getAmount());
        // simulate processing
        boolean success = Math.random() > 0.2; // 80% success

        if (success) {
            Payment p = Payment.builder()
                    .orderId(event.getOrderId())
                    .userId(event.getUserId())
                    .amount(event.getAmount())
                    .status(PaymentStatus.COMPLETED)
                    .build();
            paymentRepository.save(p);

            PaymentCompletedEvent completed = PaymentCompletedEvent.builder()
                    .orderId(event.getOrderId())
                    .userId(event.getUserId())
                    .amount(event.getAmount())
                    .timestamp(Instant.now().toEpochMilli())
                    .build();

            kafkaTemplate.send("payment.completed", event.getOrderId().toString(), completed);
            log.info("Published payment.completed for order {}", event.getOrderId());
        } else {
            PaymentFailedEvent failed = PaymentFailedEvent.builder()
                    .orderId(event.getOrderId())
                    .userId(event.getUserId())
                    .reason("Simulated failure")
                    .timestamp(Instant.now().toEpochMilli())
                    .build();

            Payment p = Payment.builder()
                    .orderId(event.getOrderId())
                    .userId(event.getUserId())
                    .amount(event.getAmount())
                    .status(PaymentStatus.FAILED)
                    .build();
            paymentRepository.save(p);

            kafkaTemplate.send("payment.failed", event.getOrderId().toString(), failed);
            log.info("Published payment.failed for order {}", event.getOrderId());
        }
    }
}
