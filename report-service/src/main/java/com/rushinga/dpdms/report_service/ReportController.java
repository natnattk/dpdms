package com.rushinga.dpdms.report_service;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/reports")
public class ReportController {

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

    @GetMapping("/csv/{hazard}")
    public ResponseEntity<String> generateCsv(@RequestHeader("Authorization") String authHeader,
                                              @PathVariable String hazard) {
                                                  
        String token = authHeader.replace("Bearer ", "");
        Claims claims = jwtUtil.extractClaims(token);
        String role = claims.get("role", String.class);
        String userHazard = claims.get("hazard", String.class);

        if (!"NATIONAL_VIEWER".equals(role) && !hazard.equalsIgnoreCase(userHazard)) {
        return ResponseEntity.status(403).body("Not authorized for this hazard's reports");
        }

        String url = HAZARD_SERVICES.get(hazard.toLowerCase());
        if (url == null) {
        return ResponseEntity.badRequest().body("Unknown hazard: " + hazard);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, entity, List.class);
        List<Map<String, Object>> incidents = response.getBody();

        StringBuilder csv = new StringBuilder();
        if (incidents == null || incidents.isEmpty()) {
            csv.append("No incidents found\n");
        } else {
            // Header row from first incident's keys
            Set<String> keys = incidents.get(0).keySet();
            csv.append(String.join(",", keys)).append("\n");
            for (Map<String, Object> incident : incidents) {
                List<String> row = new ArrayList<>();
                for (String key : keys) {
                    Object val = incident.get(key);
                    row.add(val != null ? val.toString().replace(",", ";") : "");
                }
                csv.append(String.join(",", row)).append("\n");
            }
        }

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "text/csv");
        responseHeaders.set("Content-Disposition", "attachment; filename=" + hazard + "-report.csv");

        return new ResponseEntity<>(csv.toString(), responseHeaders, HttpStatus.OK);
    }
}