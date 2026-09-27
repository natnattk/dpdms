package com.rushinga.dpdms.mining_service;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mining_incidents")
public class MiningIncident {

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

// Mining-specific indicators
private String mineName;
private String mineType; // formal or artisanal
private String accidentType; // collapse, gas explosion, flooding, fall of ground
private Integer trappedOrInjuredMiners;
private Integer fatalities;
private Boolean rescueOngoing;

public MiningIncident() {}

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

public String getMineName() { return mineName; }
public void setMineName(String mineName) { this.mineName = mineName; }

public String getMineType() { return mineType; }
public void setMineType(String mineType) { this.mineType = mineType; }

public String getAccidentType() { return accidentType; }
public void setAccidentType(String accidentType) { this.accidentType = accidentType; }

public Integer getTrappedOrInjuredMiners() { return trappedOrInjuredMiners; }
public void setTrappedOrInjuredMiners(Integer trappedOrInjuredMiners) { this.trappedOrInjuredMiners = trappedOrInjuredMiners; }

public Integer getFatalities() { return fatalities; }
public void setFatalities(Integer fatalities) { this.fatalities = fatalities; }

public Boolean getRescueOngoing() { return rescueOngoing; }
public void setRescueOngoing(Boolean rescueOngoing) { this.rescueOngoing = rescueOngoing; }
}
