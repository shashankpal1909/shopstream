package com.shashank.payment_service.repo;

import com.shashank.payment_service.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
}
