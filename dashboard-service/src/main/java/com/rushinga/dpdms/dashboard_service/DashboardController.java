package com.rushinga.dpdms.dashboard_service;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

@Autowired
private RestTemplate restTemplate;

@Autowired
private JwtUtil jwtUtil;

private static final Map<String, String> HAZARD_SERVICES = Map.of(
"flood", "http://FLOOD-SERVICE/flood-incidents",
"drought", "http://DROUGHT-SERVICE/drought-incidents",
"fire", "http://FIRE-SERVICE/fire-incidents",
"zoonotic", "http://ZOONOTIC-SERVICE/zoonotic-incidents",
"mining", "http://MINING-SERVICE/mining-incidents"
);

@GetMapping("/summary")
public ResponseEntity<?> getSummary(@RequestHeader("Authorization") String authHeader) {
String token = authHeader.replace("Bearer ", "");
Claims claims = jwtUtil.extractClaims(token);
String role = claims.get("role", String.class);
String userHazard = claims.get("hazard", String.class);

HttpHeaders headers = new HttpHeaders();
headers.set("Authorization", authHeader);
HttpEntity<Void> entity = new HttpEntity<>(headers);

Map<String, Object> summary = new LinkedHashMap<>();
int totalIncidents = 0;

for (Map.Entry<String, String> entry : HAZARD_SERVICES.entrySet()) {
String hazardKey = entry.getKey();
String url = entry.getValue();

// National viewers see all hazards; everyone else sees only their own
if (!"NATIONAL_VIEWER".equals(role) && !hazardKey.equalsIgnoreCase(userHazard)) {
continue;
}

try {
ResponseEntity<List> response = restTemplate.exchange(
url, HttpMethod.GET, entity, List.class);
List<?> incidents = response.getBody();
int count = incidents != null ? incidents.size() : 0;

Map<String, Object> hazardSummary = new LinkedHashMap<>();
hazardSummary.put("count", count);
hazardSummary.put("incidents", incidents);

summary.put(hazardKey, hazardSummary);
totalIncidents += count;
} catch (Exception e) {
Map<String, Object> errorSummary = new LinkedHashMap<>();
errorSummary.put("error", "Could not reach " + hazardKey + "-service");
summary.put(hazardKey, errorSummary);
}
}

Map<String, Object> result = new LinkedHashMap<>();
result.put("totalIncidents", totalIncidents);
result.put("hazards", summary);

return ResponseEntity.ok(result);
}
}
