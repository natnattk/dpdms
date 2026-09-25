package com.rushinga.dpdms.auth_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

@Autowired
private UserRepository userRepository;

@Autowired
private PasswordEncoder passwordEncoder;

@Autowired
private JwtUtil jwtUtil;

@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginRequest request) {
var userOpt = userRepository.findByUsername(request.getUsername());


if (userOpt.isEmpty()) {
return ResponseEntity.status(401).body("Invalid username or password");
}

User user = userOpt.get();

if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
return ResponseEntity.status(401).body("Invalid username or password");
}

String token = jwtUtil.generateToken(user);
return ResponseEntity.ok(new LoginResponse(token));
}

@PostMapping("/register")
public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
if (userRepository.findByUsername(request.getUsername()).isPresent()) {
return ResponseEntity.status(409).body("Username already exists");
}

User user = new User();
user.setUsername(request.getUsername());
user.setPassword(passwordEncoder.encode(request.getPassword()));
user.setRole(request.getRole());
user.setHazard(request.getHazard());
user.setWard(request.getWard());
user.setDistrict(request.getDistrict());

userRepository.save(user);
return ResponseEntity.ok("User created");
}
}
