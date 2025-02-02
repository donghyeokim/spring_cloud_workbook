package com.example.catalogservice.messagequeue;

import com.example.catalogservice.jpa.CatalogEntity;
import com.example.catalogservice.jpa.CatalogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {
    private final CatalogRepository repository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "example-catalog-topic")
    public void updateQty(String message) {
        log.info("Kafka consumer received message: {}", message);

        Map<Object, Object> map = new HashMap<>();
        try {
            map = objectMapper.readValue(message, new TypeReference<HashMap<Object, Object>>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        CatalogEntity entity = repository.findByProductId((String) map.get("productId"));
        if (entity != null) {
            entity.setStock(entity.getStock() - (Integer) map.get("qty"));
            repository.save(entity);
        }
    }
}
