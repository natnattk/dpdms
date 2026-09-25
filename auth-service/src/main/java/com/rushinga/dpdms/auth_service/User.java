package com.rushinga.dpdms.auth_service;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

@Column(nullable = false, unique = true)
private String username;

@Column(nullable = false)
private String password;

@Enumerated(EnumType.STRING)
@Column(nullable = false)
private Role role;

@Enumerated(EnumType.STRING)
private Hazard hazard;

private String ward;

private String district;

public User() {}

// Getters and setters
public Long getId() { return id; }
public void setId(Long id) { this.id = id; }

public String getUsername() { return username; }
public void setUsername(String username) { this.username = username; }

public String getPassword() { return password; }
public void setPassword(String password) { this.password = password; }

public Role getRole() { return role; }
public void setRole(Role role) { this.role = role; }

public Hazard getHazard() { return hazard; }
public void setHazard(Hazard hazard) { this.hazard = hazard; }

public String getWard() { return ward; }
public void setWard(String ward) { this.ward = ward; }

public String getDistrict() { return district; }
public void setDistrict(String district) { this.district = district; }
}
