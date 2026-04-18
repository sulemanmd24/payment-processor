package com.payment.processor.payment_processor.kafka.consumer;

import com.payment.processor.payment_processor.kafka.PaymentEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentEventConsumer {

    @KafkaListener(
            topics = "${payment-processor.kafka.consumer.payment-submitted.topic}",
            groupId = "${payment-processor.kafka.consumer.payment-submitted.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumePaymentRequest(
            ConsumerRecord<String, PaymentEvent> record,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String receivedTopic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        PaymentEvent event = record.value();
        log.info("Received payment request event from topic [{}], partition [{}], offset [{}]: {}",
                receivedTopic, partition, offset, event);

        try {
            processPaymentRequest(event);
        } catch (Exception e) {
            log.error("Error processing payment request event [{}]: {}", event.getPaymentId(), e.getMessage(), e);
        }
    }

    private void processPaymentRequest(PaymentEvent event) {
        log.info("Processing payment request for paymentId [{}], amount [{}] {}",
                event.getPaymentId(), event.getAmount(), event.getCurrency());
        // TODO: Add business logic for handling payment requests
    }

    private void processPaymentResponse(PaymentEvent event) {
        log.info("Processing payment response for paymentId [{}], status [{}]",
                event.getPaymentId(), event.getStatus());
        // TODO: Add business logic for handling payment responses
    }
}

