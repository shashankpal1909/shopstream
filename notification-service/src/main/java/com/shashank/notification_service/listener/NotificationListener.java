package com.shashank.notification_service.listener;

import com.shashank.notification_service.events.PaymentCompletedEvent;
import com.shashank.notification_service.events.PaymentFailedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationListener {

    @KafkaListener(topics = "payment.completed", groupId = "notification-service-group",
            containerFactory = "paymentCompletedListenerFactory")
    public void onCompleted(PaymentCompletedEvent e) {
        log.info("📩 PAYMENT SUCCESS for order {} user {} amount {}", e.getOrderId(), e.getUserId(), e.getAmount());
    }

    @KafkaListener(topics = "payment.failed", groupId = "notification-service-group",
            containerFactory = "paymentFailedListenerFactory")
    public void onFailed(PaymentFailedEvent e) {
        log.info("📩 PAYMENT FAILED for order {} user {} reason={}", e.getOrderId(), e.getUserId(), e.getReason());
    }
}
