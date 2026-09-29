package com.rushinga.dpdms.alert_service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alerts")
public class AlertController {

@Autowired
private RabbitTemplate rabbitTemplate;

@Autowired
private AlertLogRepository alertLogRepository;

@Value("${app.rabbitmq.exchange}")
private String exchange;

@Value("${app.rabbitmq.routing-key}")
private String routingKey;

@PostMapping("/trigger")
public ResponseEntity<?> trigger(@RequestBody AlertMessage message) {
rabbitTemplate.convertAndSend(exchange, routingKey, message);
return ResponseEntity.ok("Alert dispatched asynchronously");
}

@GetMapping("/log")
public ResponseEntity<List<AlertLog>> getLog() {
return ResponseEntity.ok(alertLogRepository.findAll());
}
}
