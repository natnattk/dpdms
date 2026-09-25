package com.rushinga.dpdms.auth_service;

public class RegisterRequest {
private String username;
private String password;
private Role role;
private Hazard hazard;
private String ward;
private String district;

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

