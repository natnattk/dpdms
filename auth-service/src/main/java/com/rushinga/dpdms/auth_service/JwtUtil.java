package com.rushinga.dpdms.auth_service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

@Value("${jwt.secret}")
private String secret;

@Value("${jwt.expiration-ms}")
private long expirationMs;

private SecretKey getSigningKey() {
return Keys.hmacShaKeyFor(secret.getBytes());
}

public String generateToken(User user) {
Date now = new Date();
Date expiry = new Date(now.getTime() + expirationMs);

var builder = Jwts.builder()
.subject(user.getUsername())
.claim("role", user.getRole().name())
.claim("ward", user.getWard())
.claim("district", user.getDistrict())
.issuedAt(now)
.expiration(expiry)
.signWith(getSigningKey());

if (user.getHazard() != null) {
builder.claim("hazard", user.getHazard().name());
}

return builder.compact();
}

public Claims extractAllClaims(String token) {
return Jwts.parser()
.verifyWith(getSigningKey())
.build()
.parseSignedClaims(token)
.getPayload();
}

public String extractUsername(String token) {
return extractAllClaims(token).getSubject();
}

public boolean isTokenValid(String token) {
try {
Claims claims = extractAllClaims(token);
return claims.getExpiration().after(new Date());
} catch (Exception e) {
return false;
}
}
}
