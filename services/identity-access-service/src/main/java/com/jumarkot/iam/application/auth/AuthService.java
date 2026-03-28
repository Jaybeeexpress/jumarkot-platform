package com.jumarkot.iam.application.auth;

import com.jumarkot.iam.domain.user.User;
import com.jumarkot.iam.domain.user.UserRepository;
import com.jumarkot.iam.domain.user.UserStatus;
import com.jumarkot.iam.web.auth.AuthResponse;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse login(String email, String password, UUID tenantId) {
        User user = userRepository
                .findByEmailAndTenantId(email, tenantId)
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (user.getStatus() == UserStatus.LOCKED) {
            throw new LockedException("Account is locked");
        }
        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new BadCredentialsException("Account is inactive");
        }
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String accessToken = tokenService.generateAccessToken(user);
        String refreshToken = tokenService.generateRefreshToken(user);
        log.info("User {} authenticated successfully for tenant {}", user.getId(), tenantId);
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(900L)
                .tokenType("Bearer")
                .build();
    }

    public AuthResponse refresh(String refreshToken) {
        Claims claims;
        try {
            claims = tokenService.validateAndParseClaims(refreshToken);
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid or expired refresh token");
        }

        if (!tokenService.isRefreshToken(claims)) {
            throw new BadCredentialsException("Provided token is not a refresh token");
        }

        UUID userId = tokenService.extractUserId(claims);
        UUID tenantId = tokenService.extractTenantId(claims);

        User user = userRepository
                .findById(userId)
                .filter(u -> u.getTenantId().equals(tenantId))
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BadCredentialsException("Account is not active");
        }

        String newAccessToken = tokenService.generateAccessToken(user);
        String newRefreshToken = tokenService.generateRefreshToken(user);
        log.info("Token refreshed for user {} in tenant {}", userId, tenantId);
        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiresIn(900L)
                .tokenType("Bearer")
                .build();
    }

    public void logout(UUID userId, UUID tenantId) {
        // In a stateless JWT setup, logout is handled client-side by discarding tokens.
        // For production, a token denylist (Redis-backed) would be integrated here.
        log.info("Logout recorded for user {} in tenant {}", userId, tenantId);
    }
}
