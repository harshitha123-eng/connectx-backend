package com.connectx.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    
    @GetMapping("/test")
    public String test() {
        System.out.println("TEST API HIT");
        return "Backend Working";
    }
 
    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestBody RegisterRequestDTO request) {

        return ResponseEntity.ok(
                authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody LoginRequestDTO request) {

        return ResponseEntity.ok(
                authService.login(request));
    }
}