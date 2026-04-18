package com.payment.processor.payment_processor.kafka.publisher;

import com.payment.processor.payment_processor.kafka.PaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventProducer {

    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    @Value("${payment-processor.kafka.producer.topics.payment-processed}")
    private String paymentProcessedTopic;


    public void publishToPaymentProcessed(PaymentEvent event) {
        publishEvent(paymentProcessedTopic, event.getPaymentId(), event);
    }

    private void publishEvent(String topic, String key, PaymentEvent event) {
        log.info("Publishing payment event to topic [{}] with key [{}]: {}", topic, key, event);

        CompletableFuture<SendResult<String, PaymentEvent>> future =
                kafkaTemplate.send(topic, key, event);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to publish payment event to topic [{}] with key [{}]: {}",
                        topic, key, ex.getMessage(), ex);
            } else {
                log.info("Successfully published payment event to topic [{}], partition [{}], offset [{}]",
                        topic,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }
}

