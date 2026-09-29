package com.rushinga.dpdms.mining_service;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Map;

@RestController
@RequestMapping("/mining-incidents")
public class MiningIncidentController {

@Autowired
private MiningIncidentRepository repository;

@Autowired
private JwtUtil jwtUtil;

private Claims getClaims(String authHeader) {
String token = authHeader.replace("Bearer ", "");
return jwtUtil.extractClaims(token);
}

private boolean isMiningAuthorized(Claims claims) {
String role = claims.get("role", String.class);
String hazard = claims.get("hazard", String.class);

if ("NATIONAL_VIEWER".equals(role)) {
return true;
}
return "MINING".equals(hazard);
}

@PostMapping
public ResponseEntity<?> create(@RequestHeader("Authorization") String authHeader,
@RequestBody MiningIncident incident) {
Claims claims = getClaims(authHeader);
String role = claims.get("role", String.class);

if ("NATIONAL_VIEWER".equals(role)) {
return ResponseEntity.status(403).body("National viewers cannot create incidents");
}
if (!isMiningAuthorized(claims)) {
return ResponseEntity.status(403).body("Not authorized for mining hazard");
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

if (!isMiningAuthorized(claims)) {
return ResponseEntity.status(403).body("Not authorized for mining hazard");
}

List<MiningIncident> incidents = repository.findAll();

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
if (!isMiningAuthorized(claims)) {
return ResponseEntity.status(403).body("Not authorized for mining hazard");
}

Optional<MiningIncident> incident = repository.findById(id);
return incident.<ResponseEntity<?>>map(ResponseEntity::ok)
.orElseGet(() -> ResponseEntity.notFound().build());
}

@PutMapping("/{id}")
public ResponseEntity<?> update(@RequestHeader("Authorization") String authHeader,
@PathVariable Long id,
@RequestBody MiningIncident updated) {
Claims claims = getClaims(authHeader);
String role = claims.get("role", String.class);

if ("NATIONAL_VIEWER".equals(role)) {
return ResponseEntity.status(403).body("National viewers cannot edit incidents");
}
if (!isMiningAuthorized(claims)) {
return ResponseEntity.status(403).body("Not authorized for mining hazard");
}

Optional<MiningIncident> existingOpt = repository.findById(id);
if (existingOpt.isEmpty()) {
return ResponseEntity.notFound().build();
}

MiningIncident existing = existingOpt.get();
existing.setMineName(updated.getMineName());
existing.setMineType(updated.getMineType());
existing.setAccidentType(updated.getAccidentType());
existing.setTrappedOrInjuredMiners(updated.getTrappedOrInjuredMiners());
existing.setFatalities(updated.getFatalities());
existing.setRescueOngoing(updated.getRescueOngoing());
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

if (!"PROVINCIAL_SUPERVISOR".equals(role) || !isMiningAuthorized(claims)) {
return ResponseEntity.status(403).body("Only the mining supervisor can approve");
}

Optional<MiningIncident> incidentOpt = repository.findById(id);
if (incidentOpt.isEmpty()) return ResponseEntity.notFound().build();

MiningIncident incident = incidentOpt.get();
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
if (!isMiningAuthorized(claims)) {
return ResponseEntity.status(403).body("Not authorized for mining hazard");
}

repository.deleteById(id);
return ResponseEntity.ok("Deleted");
}

@PutMapping("/{id}/reject")
public ResponseEntity<?> reject(@RequestHeader("Authorization") String authHeader,
                                 @PathVariable Long id,
                                 @RequestBody Map<String, String> body) {
    Claims claims = getClaims(authHeader);
    String role = claims.get("role", String.class);

    if (!"PROVINCIAL_SUPERVISOR".equals(role) || !isMiningAuthorized(claims)) {
        return ResponseEntity.status(403).body("Only the mining supervisor can reject");
    }

    Optional<MiningIncident> incidentOpt = repository.findById(id);
    if (incidentOpt.isEmpty()) return ResponseEntity.notFound().build();

    MiningIncident incident = incidentOpt.get();
    incident.setStatus("REJECTED");
    repository.save(incident);
    return ResponseEntity.ok(incident);
}

@PutMapping("/{id}/request-correction")
public ResponseEntity<?> requestCorrection(@RequestHeader("Authorization") String authHeader,
                                            @PathVariable Long id) {
    Claims claims = getClaims(authHeader);
    String role = claims.get("role", String.class);

    if (!"PROVINCIAL_SUPERVISOR".equals(role) || !isMiningAuthorized(claims)) {
        return ResponseEntity.status(403).body("Only the mining supervisor can request corrections");
    }

    Optional<MiningIncident> incidentOpt = repository.findById(id);
    if (incidentOpt.isEmpty()) return ResponseEntity.notFound().build();

    MiningIncident incident = incidentOpt.get();
    incident.setStatus("CORRECTION_REQUESTED");
    repository.save(incident);
    return ResponseEntity.ok(incident);
}
}

