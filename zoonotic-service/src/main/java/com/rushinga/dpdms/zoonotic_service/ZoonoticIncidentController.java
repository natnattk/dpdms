package com.rushinga.dpdms.zoonotic_service;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/zoonotic-incidents")
public class ZoonoticIncidentController {

@Autowired
private ZoonoticIncidentRepository repository;

@Autowired
private JwtUtil jwtUtil;

private Claims getClaims(String authHeader) {
String token = authHeader.replace("Bearer ", "");
return jwtUtil.extractClaims(token);
}

private boolean isZoonoticAuthorized(Claims claims) {
String role = claims.get("role", String.class);
String hazard = claims.get("hazard", String.class);

if ("NATIONAL_VIEWER".equals(role)) {
return true;
}
return "ZOONOTIC".equals(hazard);
}

@PostMapping
public ResponseEntity<?> create(@RequestHeader("Authorization") String authHeader,
@RequestBody ZoonoticIncident incident) {
Claims claims = getClaims(authHeader);
String role = claims.get("role", String.class);

if ("NATIONAL_VIEWER".equals(role)) {
return ResponseEntity.status(403).body("National viewers cannot create incidents");
}
if (!isZoonoticAuthorized(claims)) {
return ResponseEntity.status(403).body("Not authorized for zoonotic hazard");
}

incident.setStatus("PENDING");
incident.setReporter(claims.getSubject());
incident.setWard(claims.get("ward", String.class));
incident.setDistrict(claims.get("district", String.class));

return ResponseEntity.ok(repository.save(incident));
}

@GetMapping
public ResponseEntity<?> getAll(@RequestHeader("Authorization") String authHeader) {
Claims claims = getClaims(authHeader);
String role = claims.get("role", String.class);

if (!isZoonoticAuthorized(claims)) {
return ResponseEntity.status(403).body("Not authorized for zoonotic hazard");
}

List<ZoonoticIncident> incidents = repository.findAll();

if ("NATIONAL_VIEWER".equals(role) || "PROVINCIAL_SUPERVISOR".equals(role)) {
incidents = incidents.stream()
.filter(i -> "APPROVED".equals(i.getStatus()) || "PENDING".equals(i.getStatus()))
.toList();
}

return ResponseEntity.ok(incidents);
}

@GetMapping("/{id}")
public ResponseEntity<?> getOne(@RequestHeader("Authorization") String authHeader,
@PathVariable Long id) {
Claims claims = getClaims(authHeader);
if (!isZoonoticAuthorized(claims)) {
return ResponseEntity.status(403).body("Not authorized for zoonotic hazard");
}

Optional<ZoonoticIncident> incident = repository.findById(id);
return incident.<ResponseEntity<?>>map(ResponseEntity::ok)
.orElseGet(() -> ResponseEntity.notFound().build());
}

@PutMapping("/{id}")
public ResponseEntity<?> update(@RequestHeader("Authorization") String authHeader,
@PathVariable Long id,
@RequestBody ZoonoticIncident updated) {
Claims claims = getClaims(authHeader);
String role = claims.get("role", String.class);

if ("NATIONAL_VIEWER".equals(role)) {
return ResponseEntity.status(403).body("National viewers cannot edit incidents");
}
if (!isZoonoticAuthorized(claims)) {
return ResponseEntity.status(403).body("Not authorized for zoonotic hazard");
}

Optional<ZoonoticIncident> existingOpt = repository.findById(id);
if (existingOpt.isEmpty()) {
return ResponseEntity.notFound().build();
}

ZoonoticIncident existing = existingOpt.get();
existing.setPathogenName(updated.getPathogenName());
existing.setAnimalSpeciesAffected(updated.getAnimalSpeciesAffected());
existing.setConfirmedHumanCases(updated.getConfirmedHumanCases());
existing.setConfirmedAnimalCases(updated.getConfirmedAnimalCases());
existing.setClassification(updated.getClassification());
existing.setSeverity(updated.getSeverity());
existing.setLatitude(updated.getLatitude());
existing.setLongitude(updated.getLongitude());
existing.setOccurredAt(updated.getOccurredAt());

return ResponseEntity.ok(repository.save(existing));
}

@PutMapping("/{id}/approve")
public ResponseEntity<?> approve(@RequestHeader("Authorization") String authHeader,
@PathVariable Long id) {
Claims claims = getClaims(authHeader);
String role = claims.get("role", String.class);

if (!"PROVINCIAL_SUPERVISOR".equals(role) || !isZoonoticAuthorized(claims)) {
return ResponseEntity.status(403).body("Only the zoonotic supervisor can approve");
}

Optional<ZoonoticIncident> incidentOpt = repository.findById(id);
if (incidentOpt.isEmpty()) return ResponseEntity.notFound().build();

ZoonoticIncident incident = incidentOpt.get();
incident.setStatus("APPROVED");
return ResponseEntity.ok(repository.save(incident));
}

@DeleteMapping("/{id}")
public ResponseEntity<?> delete(@RequestHeader("Authorization") String authHeader,
@PathVariable Long id) {
Claims claims = getClaims(authHeader);
String role = claims.get("role", String.class);

if ("NATIONAL_VIEWER".equals(role)) {
return ResponseEntity.status(403).body("National viewers cannot delete incidents");
}
if (!isZoonoticAuthorized(claims)) {
return ResponseEntity.status(403).body("Not authorized for zoonotic hazard");
}

repository.deleteById(id);
return ResponseEntity.ok("Deleted");
}
}


