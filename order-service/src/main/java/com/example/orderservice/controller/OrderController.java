package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.jpa.OrderEntity;
import com.example.orderservice.messagequeue.KafkaProducer;
import com.example.orderservice.messagequeue.OrderProducer;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.vo.RequestOrder;
import com.example.orderservice.vo.ResponseOrder;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Slf4j
@RestController
@RequestMapping("/order-service")
@RequiredArgsConstructor
public class OrderController {
    private final Environment env;
    private final OrderService orderService;
    private final ModelMapper mapper;
    private final KafkaProducer kafkaProducer;
    private final OrderProducer orderProducer;

    @GetMapping("/health_check")
    @Timed(value = "users.status", longTask = true)
    public String status() {
        return String.format("It's Working in Order Service on PORT %s", env.getProperty("local.server.port"));
    }

    @PostMapping("/{userId}/orders")
    public ResponseEntity<ResponseOrder> createOrder(@PathVariable String userId,
                                                     @RequestBody RequestOrder order) {
        log.info("Before createOrder");
        OrderDto orderDto = mapper.map(order, OrderDto.class);
        orderDto.setUserId(userId);

        /* JPA 방식 */
        OrderDto createdOrder = orderService.createOrder(orderDto);
        ResponseOrder responseOrder = mapper.map(createdOrder, ResponseOrder.class);

//        orderDto.setOrderId(UUID.randomUUID().toString());
//        orderDto.setTotalPrice(orderDto.getQty() * orderDto.getUnitPrice());
//        ResponseOrder responseOrder = mapper.map(orderDto, ResponseOrder.class);

        /* send message to kafka */
//        orderProducer.send("orders", orderDto);
//        kafkaProducer.send("example-catalog-topic", orderDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseOrder);
    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<ResponseOrder>> getOrders(@PathVariable String userId) {
        log.info("Before call getOrders");
        Iterable<OrderEntity> ordersByUserId = orderService.getOrdersByUserId(userId);
        log.info("After call getOrders");
        return ResponseEntity.status(HttpStatus.OK).body(
            StreamSupport.stream(ordersByUserId.spliterator(), false)
                    .map(orderEntity -> mapper.map(orderEntity, ResponseOrder.class))
                    .toList()
        );
    }
}
