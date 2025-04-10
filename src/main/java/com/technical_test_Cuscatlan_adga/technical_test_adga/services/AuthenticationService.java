package com.technical_test_Cuscatlan_adga.technical_test_adga.services;

import com.technical_test_Cuscatlan_adga.technical_test_adga.models.enums.RoleType;
import com.technical_test_Cuscatlan_adga.technical_test_adga.models.repositories.UserRepository;
import com.technical_test_Cuscatlan_adga.technical_test_adga.security.User;
import com.technical_test_Cuscatlan_adga.technical_test_adga.security.auth.AuthRequest;
import com.technical_test_Cuscatlan_adga.technical_test_adga.security.auth.AuthResponse;
import com.technical_test_Cuscatlan_adga.technical_test_adga.security.auth.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register (RegisterRequest request) {
        var user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(RoleType.valueOf(request.getRole()))
                .active(true)
                .build();
        userRepository.save(user);

        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }
}
