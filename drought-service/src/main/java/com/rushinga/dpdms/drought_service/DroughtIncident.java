package com.rushinga.dpdms.drought_service;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "drought_incidents")
public class DroughtIncident {

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

// Drought-specific indicators
private Double rainfallDeficitMm;
private Integer consecutiveDryDays;
private Double cropFailurePercentage;
private Integer peopleFacingWaterShortages;
private Integer livestockMortalityCount;

public DroughtIncident() {}

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

public Double getRainfallDeficitMm() { return rainfallDeficitMm; }
public void setRainfallDeficitMm(Double rainfallDeficitMm) { this.rainfallDeficitMm = rainfallDeficitMm; }

public Integer getConsecutiveDryDays() { return consecutiveDryDays; }
public void setConsecutiveDryDays(Integer consecutiveDryDays) { this.consecutiveDryDays = consecutiveDryDays; }

public Double getCropFailurePercentage() { return cropFailurePercentage; }
public void setCropFailurePercentage(Double cropFailurePercentage) { this.cropFailurePercentage = cropFailurePercentage; }

public Integer getPeopleFacingWaterShortages() { return peopleFacingWaterShortages; }
public void setPeopleFacingWaterShortages(Integer peopleFacingWaterShortages) { this.peopleFacingWaterShortages = peopleFacingWaterShortages; }

public Integer getLivestockMortalityCount() { return livestockMortalityCount; }
public void setLivestockMortalityCount(Integer livestockMortalityCount) { this.livestockMortalityCount = livestockMortalityCount; }
}
