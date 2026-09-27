package com.rushinga.dpdms.zoonotic_service;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "zoonotic_incidents")
public class ZoonoticIncident {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

private String ward;
private String district;
private String province;
private LocalDateTime occurredAt;
private String reporter;
private String severity;
private String status;
private Double latitude;
private Double longitude;

// Zoonotic-specific indicators
private String pathogenName;
private String animalSpeciesAffected;
private Integer confirmedHumanCases;
private Integer confirmedAnimalCases;
private String classification; // CLUSTER or OUTBREAK

public ZoonoticIncident() {}

public Long getId() { return id; }
public void setId(Long id) { this.id = id; }

public String getWard() { return ward; }
public void setWard(String ward) { this.ward = ward; }

public String getDistrict() { return district; }
public void setDistrict(String district) { this.district = district; }

public String getProvince() { return province; }
public void setProvince(String province) { this.province = province; }

public LocalDateTime getOccurredAt() { return occurredAt; }
public void setOccurredAt(LocalDateTime occurredAt) { this.occurredAt = occurredAt; }

public String getReporter() { return reporter; }
public void setReporter(String reporter) { this.reporter = reporter; }

public String getSeverity() { return severity; }
public void setSeverity(String severity) { this.severity = severity; }

public String getStatus() { return status; }
public void setStatus(String status) { this.status = status; }

public Double getLatitude() { return latitude; }
public void setLatitude(Double latitude) { this.latitude = latitude; }

public Double getLongitude() { return longitude; }
public void setLongitude(Double longitude) { this.longitude = longitude; }

public String getPathogenName() { return pathogenName; }
public void setPathogenName(String pathogenName) { this.pathogenName = pathogenName; }

public String getAnimalSpeciesAffected() { return animalSpeciesAffected; }
public void setAnimalSpeciesAffected(String animalSpeciesAffected) { this.animalSpeciesAffected = animalSpeciesAffected; }

public Integer getConfirmedHumanCases() { return confirmedHumanCases; }
public void setConfirmedHumanCases(Integer confirmedHumanCases) { this.confirmedHumanCases = confirmedHumanCases; }

public Integer getConfirmedAnimalCases() { return confirmedAnimalCases; }
public void setConfirmedAnimalCases(Integer confirmedAnimalCases) { this.confirmedAnimalCases = confirmedAnimalCases; }

public String getClassification() { return classification; }
public void setClassification(String classification) { this.classification = classification; }
}
