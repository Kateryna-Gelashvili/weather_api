package org.k.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.k.data.Weather;
import org.k.exception.UnknownException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PreDestroy;

@Service
public class KafkaProducerService {
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

    private final Producer<String, String> producer;
    private final ObjectMapper objectMapper;

    private KafkaProducerService(ObjectMapper objectMapper,
                                 @Value("${kafka.address}") String address) throws IOException {
        this.objectMapper = objectMapper;
        Properties props = new Properties();
        props.put("bootstrap.servers", address);
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        this.producer = new KafkaProducer<>(props);
    }

    @PreDestroy
    private void closeProducer() {
        producer.close();
    }

    public Producer getProducer() {
        return this.producer;
    }

    public void sendWeatherMessages(Map<String, Weather> messages) {
        logger.info("Sending weather messages...");
        for (Map.Entry message : messages.entrySet()) {
            try {
                logger.info("Send message for topic: {}", message.getKey());
                producer.send(new ProducerRecord<>(message.getKey().toString(),
                        objectMapper.writeValueAsString(message.getValue())));
                logger.info("Message sent for topic: {}. Message: {}", message.getKey(),
                        message.getValue());
            } catch (JsonProcessingException e) {
                logger.error(e.getMessage(), e);
                throw new UnknownException("JsonProcessingException while parsing " +
                        message.getValue());
            }
        }
        logger.info("Flushing the messages to the producer...");
        producer.flush();
        logger.info("Successfully flushed the messages to the producer...");
    }
}
