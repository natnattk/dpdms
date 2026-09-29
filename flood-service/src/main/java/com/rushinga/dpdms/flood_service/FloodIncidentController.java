package com.rushinga.dpdms.flood_service;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Map;

@RestController
@RequestMapping("/flood-incidents")
public class FloodIncidentController {

@Autowired
private FloodIncidentRepository repository;

@Autowired
private JwtUtil jwtUtil;

private Claims getClaims(String authHeader) {
String token = authHeader.replace("Bearer ", "");
return jwtUtil.extractClaims(token);
}

private boolean isFloodAuthorized(Claims claims) {
String role = claims.get("role", String.class);
String hazard = claims.get("hazard", String.class);

if ("NATIONAL_VIEWER".equals(role)) {
return true; // read-only, checked separately per method
}
return "FLOOD".equals(hazard);
}

@PostMapping
public ResponseEntity<?> create(@RequestHeader("Authorization") String authHeader,
@RequestBody FloodIncident incident) {
Claims claims = getClaims(authHeader);
String role = claims.get("role", String.class);

if ("NATIONAL_VIEWER".equals(role)) {
return ResponseEntity.status(403).body("National viewers cannot create incidents");
}
if (!isFloodAuthorized(claims)) {
return ResponseEntity.status(403).body("Not authorized for flood hazard");
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

if (!isFloodAuthorized(claims)) {
return ResponseEntity.status(403).body("Not authorized for flood hazard");
}

List<FloodIncident> incidents = repository.findAll();

// National viewers and supervisors see only approved; recorders see their own too
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
if (!isFloodAuthorized(claims)) {
return ResponseEntity.status(403).body("Not authorized for flood hazard");
}

Optional<FloodIncident> incident = repository.findById(id);
return incident.<ResponseEntity<?>>map(ResponseEntity::ok)
.orElseGet(() -> ResponseEntity.notFound().build());
}

@PutMapping("/{id}")
public ResponseEntity<?> update(@RequestHeader("Authorization") String authHeader,
@PathVariable Long id,
@RequestBody FloodIncident updated) {
Claims claims = getClaims(authHeader);
String role = claims.get("role", String.class);

if ("NATIONAL_VIEWER".equals(role)) {
return ResponseEntity.status(403).body("National viewers cannot edit incidents");
}
if (!isFloodAuthorized(claims)) {
return ResponseEntity.status(403).body("Not authorized for flood hazard");
}

Optional<FloodIncident> existingOpt = repository.findById(id);
if (existingOpt.isEmpty()) {
return ResponseEntity.notFound().build();
}

FloodIncident existing = existingOpt.get();
existing.setPeakWaterLevelMetres(updated.getPeakWaterLevelMetres());
existing.setRiverBasin(updated.getRiverBasin());
existing.setHouseholdsDisplaced(updated.getHouseholdsDisplaced());
existing.setAreaFloodedHectares(updated.getAreaFloodedHectares());
existing.setDurationDays(updated.getDurationDays());
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

if (!"PROVINCIAL_SUPERVISOR".equals(role) || !isFloodAuthorized(claims)) {
return ResponseEntity.status(403).body("Only the flood supervisor can approve");
}

Optional<FloodIncident> incidentOpt = repository.findById(id);
if (incidentOpt.isEmpty()) return ResponseEntity.notFound().build();

FloodIncident incident = incidentOpt.get();
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
if (!isFloodAuthorized(claims)) {
return ResponseEntity.status(403).body("Not authorized for flood hazard");
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

    if (!"PROVINCIAL_SUPERVISOR".equals(role) || !isFloodAuthorized(claims)) {
        return ResponseEntity.status(403).body("Only the flood supervisor can reject");
    }

    Optional<FloodIncident> incidentOpt = repository.findById(id);
    if (incidentOpt.isEmpty()) return ResponseEntity.notFound().build();

    FloodIncident incident = incidentOpt.get();
    incident.setStatus("REJECTED");
    repository.save(incident);
    return ResponseEntity.ok(incident);
}

@PutMapping("/{id}/request-correction")
public ResponseEntity<?> requestCorrection(@RequestHeader("Authorization") String authHeader,
                                            @PathVariable Long id) {
    Claims claims = getClaims(authHeader);
    String role = claims.get("role", String.class);

    if (!"PROVINCIAL_SUPERVISOR".equals(role) || !isFloodAuthorized(claims)) {
        return ResponseEntity.status(403).body("Only the flood supervisor can request corrections");
    }

    Optional<FloodIncident> incidentOpt = repository.findById(id);
    if (incidentOpt.isEmpty()) return ResponseEntity.notFound().build();

    FloodIncident incident = incidentOpt.get();
    incident.setStatus("CORRECTION_REQUESTED");
    repository.save(incident);
    return ResponseEntity.ok(incident);
}

}


