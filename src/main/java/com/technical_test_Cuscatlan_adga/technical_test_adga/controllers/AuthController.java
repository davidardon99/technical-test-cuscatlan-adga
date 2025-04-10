package com.technical_test_Cuscatlan_adga.technical_test_adga.controllers;

import com.technical_test_Cuscatlan_adga.technical_test_adga.security.auth.AuthRequest;
import com.technical_test_Cuscatlan_adga.technical_test_adga.security.auth.AuthResponse;
import com.technical_test_Cuscatlan_adga.technical_test_adga.security.auth.RegisterRequest;
import com.technical_test_Cuscatlan_adga.technical_test_adga.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/v1")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
}
