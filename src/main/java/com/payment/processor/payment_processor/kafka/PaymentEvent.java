package com.payment.processor.payment_processor.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent {

    private String paymentId;
    private String senderId;
    private String receiverId;
    private BigDecimal amount;
    private String currency;
    private String status;
    private LocalDateTime timestamp;
}

