package com.payment.processor.payment_processor.repository;

import com.payment.processor.payment_processor.entity.PaymentRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRecordRepository extends JpaRepository<PaymentRecordEntity, Long> {

    Optional<PaymentRecordEntity> findByPaymentId(UUID paymentId);

    List<PaymentRecordEntity> findByDebitAccountId(String debitAccountId);

    List<PaymentRecordEntity> findByCreditAccountId(String creditAccountId);

    List<PaymentRecordEntity> findByStatus(String status);

    List<PaymentRecordEntity> findByProcessedAtBetween(Instant from, Instant to);
}

