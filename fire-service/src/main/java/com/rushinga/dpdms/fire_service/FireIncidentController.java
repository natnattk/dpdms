package com.rushinga.dpdms.fire_service;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Map;

@RestController
@RequestMapping("/fire-incidents")
public class FireIncidentController {

@Autowired
private FireIncidentRepository repository;

@Autowired
private JwtUtil jwtUtil;

private Claims getClaims(String authHeader) {
String token = authHeader.replace("Bearer ", "");
return jwtUtil.extractClaims(token);
}

private boolean isFireAuthorized(Claims claims) {
String role = claims.get("role", String.class);
String hazard = claims.get("hazard", String.class);

if ("NATIONAL_VIEWER".equals(role)) {
return true;
}
return "FIRE".equals(hazard);
}

@PostMapping
public ResponseEntity<?> create(@RequestHeader("Authorization") String authHeader,
@RequestBody FireIncident incident) {
Claims claims = getClaims(authHeader);
String role = claims.get("role", String.class);

if ("NATIONAL_VIEWER".equals(role)) {
return ResponseEntity.status(403).body("National viewers cannot create incidents");
}
if (!isFireAuthorized(claims)) {
return ResponseEntity.status(403).body("Not authorized for fire hazard");
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

if (!isFireAuthorized(claims)) {
return ResponseEntity.status(403).body("Not authorized for fire hazard");
}

List<FireIncident> incidents = repository.findAll();

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
if (!isFireAuthorized(claims)) {
return ResponseEntity.status(403).body("Not authorized for fire hazard");
}

Optional<FireIncident> incident = repository.findById(id);
return incident.<ResponseEntity<?>>map(ResponseEntity::ok)
.orElseGet(() -> ResponseEntity.notFound().build());
}

@PutMapping("/{id}")
public ResponseEntity<?> update(@RequestHeader("Authorization") String authHeader,
@PathVariable Long id,
@RequestBody FireIncident updated) {
Claims claims = getClaims(authHeader);
String role = claims.get("role", String.class);

if ("NATIONAL_VIEWER".equals(role)) {
return ResponseEntity.status(403).body("National viewers cannot edit incidents");
}
if (!isFireAuthorized(claims)) {
return ResponseEntity.status(403).body("Not authorized for fire hazard");
}

Optional<FireIncident> existingOpt = repository.findById(id);
if (existingOpt.isEmpty()) {
return ResponseEntity.notFound().build();
}

FireIncident existing = existingOpt.get();
existing.setAreaBurnedHectares(updated.getAreaBurnedHectares());
existing.setSuspectedCause(updated.getSuspectedCause());
existing.setInjuriesOrFatalities(updated.getInjuriesOrFatalities());
existing.setStructuresDestroyed(updated.getStructuresDestroyed());
existing.setStillActive(updated.getStillActive());
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

if (!"PROVINCIAL_SUPERVISOR".equals(role) || !isFireAuthorized(claims)) {
return ResponseEntity.status(403).body("Only the fire supervisor can approve");
}

Optional<FireIncident> incidentOpt = repository.findById(id);
if (incidentOpt.isEmpty()) return ResponseEntity.notFound().build();

FireIncident incident = incidentOpt.get();
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
if (!isFireAuthorized(claims)) {
return ResponseEntity.status(403).body("Not authorized for fire hazard");
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

    if (!"PROVINCIAL_SUPERVISOR".equals(role) || !isFireAuthorized(claims)) {
        return ResponseEntity.status(403).body("Only the fire supervisor can reject");
    }

    Optional<FireIncident> incidentOpt = repository.findById(id);
    if (incidentOpt.isEmpty()) return ResponseEntity.notFound().build();

    FireIncident incident = incidentOpt.get();
    incident.setStatus("REJECTED");
    repository.save(incident);
    return ResponseEntity.ok(incident);
}

@PutMapping("/{id}/request-correction")
public ResponseEntity<?> requestCorrection(@RequestHeader("Authorization") String authHeader,
                                            @PathVariable Long id) {
    Claims claims = getClaims(authHeader);
    String role = claims.get("role", String.class);

    if (!"PROVINCIAL_SUPERVISOR".equals(role) || !isFireAuthorized(claims)) {
        return ResponseEntity.status(403).body("Only the fire supervisor can request corrections");
    }

    Optional<FireIncident> incidentOpt = repository.findById(id);
    if (incidentOpt.isEmpty()) return ResponseEntity.notFound().build();

    FireIncident incident = incidentOpt.get();
    incident.setStatus("CORRECTION_REQUESTED");
    repository.save(incident);
    return ResponseEntity.ok(incident);
}
}
