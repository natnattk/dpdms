package com.rushinga.dpdms.flood_service;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "flood_incidents")
public class FloodIncident {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

// Shared incident metadata
private String ward;
private String district;
private String province;
private LocalDateTime occurredAt;
private String reporter;
private String severity;
private String status; // PENDING, APPROVED, REJECTED, CORRECTION_REQUESTED
private Double latitude;
private Double longitude;

// Flood-specific indicators
private Double peakWaterLevelMetres;
private String riverBasin;
private Integer householdsDisplaced;
private Double areaFloodedHectares;
private Integer durationDays;

public FloodIncident() {}

// Getters and setters
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

public Double getPeakWaterLevelMetres() { return peakWaterLevelMetres; }
public void setPeakWaterLevelMetres(Double peakWaterLevelMetres) { this.peakWaterLevelMetres = peakWaterLevelMetres; }

public String getRiverBasin() { return riverBasin; }
public void setRiverBasin(String riverBasin) { this.riverBasin = riverBasin; }

public Integer getHouseholdsDisplaced() { return householdsDisplaced; }
public void setHouseholdsDisplaced(Integer householdsDisplaced) { this.householdsDisplaced = householdsDisplaced; }

public Double getAreaFloodedHectares() { return areaFloodedHectares; }
public void setAreaFloodedHectares(Double areaFloodedHectares) { this.areaFloodedHectares = areaFloodedHectares; }

public Integer getDurationDays() { return durationDays; }
public void setDurationDays(Integer durationDays) { this.durationDays = durationDays; }
}

