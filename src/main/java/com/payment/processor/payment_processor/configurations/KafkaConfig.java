package com.payment.processor.payment_processor.configurations;

import com.payment.processor.payment_processor.kafka.PaymentEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    // Address of the Kafka broker(s) that producers and consumers connect to for publishing and consuming messages.
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, PaymentEvent> producerFactory() {
        Map<String, Object> props = new HashMap<>();

        // Tells the producer which Kafka broker(s) to connect to for bootstrapping the cluster metadata.
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        // Serializes the message key (paymentId) to bytes before sending it to Kafka.
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        // Serializes the PaymentEvent object to JSON bytes before sending it to Kafka.
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        // Requires acknowledgement from all in-sync replicas before considering a send successful, maximizing durability.
        props.put(ProducerConfig.ACKS_CONFIG, "all");

        // Automatically retries failed sends up to 3 times to handle transient broker errors.
        props.put(ProducerConfig.RETRIES_CONFIG, 3);

        // Enables idempotent producing so that retries never result in duplicate messages in the topic.
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

        // Prevents the serializer from adding Spring type headers to messages, keeping the payload clean and framework-agnostic.
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);

        return new DefaultKafkaProducerFactory<>(props);
    }


    @Bean
    public KafkaTemplate<String, PaymentEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }


    @Bean
    public ConsumerFactory<String, PaymentEvent> consumerFactory() {
        JsonDeserializer<PaymentEvent> deserializer = new JsonDeserializer<>(PaymentEvent.class);

        // Keeps Spring type headers in the consumed record so downstream components can inspect the original type if needed.
        deserializer.setRemoveTypeHeaders(false);

        // Allows deserialization of classes from any package, preventing ClassNotTrustedException for PaymentEvent.
        deserializer.addTrustedPackages("*");

        // Instructs the deserializer to use the type mapper for the key as well, ensuring consistent type resolution.
        deserializer.setUseTypeMapperForKey(true);

        Map<String, Object> props = new HashMap<>();

        // Broker address used by the consumer to discover the Kafka cluster.
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        // When no committed offset exists, start reading from the earliest available message rather than skipping old ones.
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // Disables automatic offset commits so offsets are only committed after successful message processing (manual ack).
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        // Deserializes the String message key from bytes back into a String.
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        // Deserializes the JSON message value from bytes back into a PaymentEvent object.
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PaymentEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        // Provides the consumer configuration (broker, deserializers, offsets) to each listener container.
        factory.setConsumerFactory(consumerFactory());

        // Spins up 3 concurrent consumer threads, allowing parallel processing across topic partitions.
        factory.setConcurrency(3);

        // Commits the offset immediately after each individual record is successfully processed, minimising reprocessing on failure.
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);

        return factory;
    }
}
