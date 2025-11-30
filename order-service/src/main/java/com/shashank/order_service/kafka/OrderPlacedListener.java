package com.shashank.order_service.kafka;

import com.shashank.order_service.events.*;
import com.shashank.order_service.domain.*;
import com.shashank.order_service.repo.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.time.Instant;

@Component
public class OrderPlacedListener {

    private final ProcessedEventRepository peRepo;
    private final OrderRepository orderRepo;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderPlacedListener(ProcessedEventRepository peRepo,
                               OrderRepository orderRepo,
                               KafkaTemplate<String, Object> kafkaTemplate) {
        this.peRepo = peRepo;
        this.orderRepo = orderRepo;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "order.placed", groupId = "order-service-group", containerFactory = "kafkaListenerContainerFactory")
    @Transactional
    public void listen(OrderPlacedEvent event) {
        String eid = event.getOrderId().toString();

        // idempotency check
        if (peRepo.findByEventId(eid).isPresent()) {
            // already processed
            return;
        }

        // persist processed-event marker first (prevents double processing on retry)
        ProcessedEvent marker = ProcessedEvent.builder()
                .eventId(eid)
                .processedAt(Instant.now())
                .build();
        peRepo.save(marker);

        // create Order entity
        Order order = Order.builder()
                .id(event.getOrderId())
                .userId(event.getUserId())
                .items(event.getItems().stream().map(it ->
                        new Order.OrderItem(it.getProductId(), it.getName(), it.getQty(), it.getPrice())
                ).collect(Collectors.toList()))
                .total(event.getTotal())
                .status(OrderStatus.PENDING)
                .build();

        orderRepo.save(order);

        // emit payment.requested
        PaymentRequestedEvent paymentEvent = PaymentRequestedEvent.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .amount(order.getTotal())
                .build();

        kafkaTemplate.send("payment.requested", order.getId().toString(), paymentEvent);

        // update order status
        order.setStatus(OrderStatus.PAYMENT_REQUESTED);
        orderRepo.save(order);
    }
}
