package com.rushinga.dpdms.alert_service;

import java.io.Serializable;

public class AlertMessage implements Serializable {
private String hazard;
private String message;
private String recipient;

public AlertMessage() {}

public AlertMessage(String hazard, String message, String recipient) {
this.hazard = hazard;
this.message = message;
this.recipient = recipient;
}

public String getHazard() { return hazard; }
public void setHazard(String hazard) { this.hazard = hazard; }
public String getMessage() { return message; }
public void setMessage(String message) { this.message = message; }
public String getRecipient() { return recipient; }
public void setRecipient(String recipient) { this.recipient = recipient; }
}