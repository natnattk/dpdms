package com.rushinga.dpdms.mining_service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtUtil {

@Value("${jwt.secret}")
private String secret;

private SecretKey getSigningKey() {
return Keys.hmacShaKeyFor(secret.getBytes());
}

public Claims extractClaims(String token) {
return Jwts.parser()
.verifyWith(getSigningKey())
.build()
.parseSignedClaims(token)
.getPayload();
}
}

