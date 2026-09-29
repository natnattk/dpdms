package com.rushinga.dpdms.alert_service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AlertListener {

@Autowired
private AlertLogRepository alertLogRepository;

@RabbitListener(queues = "${app.rabbitmq.queue}")
public void handleAlert(AlertMessage alertMessage) {
// Simulated dispatch - real WhatsApp/email integration would go here
System.out.println("[ALERT] Hazard: " + alertMessage.getHazard()
+ " | Message: " + alertMessage.getMessage()
+ " | To: " + alertMessage.getRecipient());

AlertLog log = new AlertLog();
log.setHazard(alertMessage.getHazard());
log.setMessage(alertMessage.getMessage());
log.setRecipient(alertMessage.getRecipient());
log.setChannel("EMAIL");
log.setSentAt(LocalDateTime.now());
log.setDeliveryStatus("SIMULATED"); // would be SENT/FAILED with real integration

alertLogRepository.save(log);

// Log a second channel too, to match brief's "email AND WhatsApp"
AlertLog log2 = new AlertLog();
log2.setHazard(alertMessage.getHazard());
log2.setMessage(alertMessage.getMessage());
log2.setRecipient(alertMessage.getRecipient());
log2.setChannel("WHATSAPP");
log2.setSentAt(LocalDateTime.now());
log2.setDeliveryStatus("SIMULATED");

alertLogRepository.save(log2);
}
}
