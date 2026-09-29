package com.rushinga.dpdms.alert_service;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alert_logs")
public class AlertLog {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

private String hazard;
private String message;
private String channel; // EMAIL or WHATSAPP
private String recipient;
private LocalDateTime sentAt;
private String deliveryStatus; // SENT, FAILED, SIMULATED

public AlertLog() {}

public Long getId() { return id; }
public void setId(Long id) { this.id = id; }
public String getHazard() { return hazard; }
public void setHazard(String hazard) { this.hazard = hazard; }
public String getMessage() { return message; }
public void setMessage(String message) { this.message = message; }
public String getChannel() { return channel; }
public void setChannel(String channel) { this.channel = channel; }
public String getRecipient() { return recipient; }
public void setRecipient(String recipient) { this.recipient = recipient; }
public LocalDateTime getSentAt() { return sentAt; }
public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
public String getDeliveryStatus() { return deliveryStatus; }
public void setDeliveryStatus(String deliveryStatus) { this.deliveryStatus = deliveryStatus; }
}