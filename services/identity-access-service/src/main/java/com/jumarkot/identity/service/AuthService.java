package com.jumarkot.identity.service;

import com.jumarkot.contracts.identity.LoginRequest;
import com.jumarkot.contracts.identity.TokenResponse;
import com.jumarkot.identity.domain.User;
import com.jumarkot.identity.repository.UserRepository;
import com.jumarkot.identity.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public TokenResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalStateException("User not found after authentication"));

        String accessToken = jwtTokenProvider.generateAccessToken(
                user.getEmail(), user.getTenantId().toString(), user.getRoles());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(900)
                .issuedAt(Instant.now())
                .build();
    }
}
