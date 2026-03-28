package com.jumarkot.iam.auth;

import com.jumarkot.iam.application.auth.AuthService;
import com.jumarkot.iam.application.auth.TokenService;
import com.jumarkot.iam.domain.user.User;
import com.jumarkot.iam.domain.user.UserRepository;
import com.jumarkot.iam.domain.user.UserRole;
import com.jumarkot.iam.domain.user.UserStatus;
import com.jumarkot.iam.web.auth.AuthResponse;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    private PasswordEncoder passwordEncoder;
    private AuthService authService;

    private final UUID userId = UUID.randomUUID();
    private final UUID tenantId = UUID.randomUUID();
    private final String rawPassword = "SecurePassword123!";
    private User activeUser;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder(4); // Lower cost for tests
        authService = new AuthService(userRepository, tokenService, passwordEncoder);

        activeUser = User.builder()
                .id(userId)
                .email("analyst@acme.com")
                .passwordHash(passwordEncoder.encode(rawPassword))
                .tenantId(tenantId)
                .roles(Set.of(UserRole.ANALYST))
                .status(UserStatus.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("login returns tokens on valid credentials")
    void login_validCredentials_returnsAuthResponse() {
        when(userRepository.findByEmailAndTenantId("analyst@acme.com", tenantId))
                .thenReturn(Optional.of(activeUser));
        when(tokenService.generateAccessToken(activeUser)).thenReturn("access-token-stub");
        when(tokenService.generateRefreshToken(activeUser)).thenReturn("refresh-token-stub");

        AuthResponse response = authService.login("analyst@acme.com", rawPassword, tenantId);

        assertThat(response.getAccessToken()).isEqualTo("access-token-stub");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token-stub");
        assertThat(response.getExpiresIn()).isEqualTo(900L);
        assertThat(response.getTokenType()).isEqualTo("Bearer");
    }

    @Test
    @DisplayName("login throws BadCredentialsException for unknown email")
    void login_unknownEmail_throwsBadCredentials() {
        when(userRepository.findByEmailAndTenantId(any(), any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login("unknown@acme.com", rawPassword, tenantId))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Invalid email or password");
    }

    @Test
    @DisplayName("login throws BadCredentialsException for wrong password")
    void login_wrongPassword_throwsBadCredentials() {
        when(userRepository.findByEmailAndTenantId("analyst@acme.com", tenantId))
                .thenReturn(Optional.of(activeUser));

        assertThatThrownBy(() -> authService.login("analyst@acme.com", "WrongPassword!", tenantId))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Invalid email or password");
    }

    @Test
    @DisplayName("login throws LockedException for locked account")
    void login_lockedUser_throwsLockedException() {
        User lockedUser = User.builder()
                .id(userId)
                .email("analyst@acme.com")
                .passwordHash(passwordEncoder.encode(rawPassword))
                .tenantId(tenantId)
                .roles(Set.of(UserRole.ANALYST))
                .status(UserStatus.LOCKED)
                .build();

        when(userRepository.findByEmailAndTenantId("analyst@acme.com", tenantId))
                .thenReturn(Optional.of(lockedUser));

        assertThatThrownBy(() -> authService.login("analyst@acme.com", rawPassword, tenantId))
                .isInstanceOf(LockedException.class)
                .hasMessageContaining("Account is locked");
    }

    @Test
    @DisplayName("login throws BadCredentialsException for inactive account")
    void login_inactiveUser_throwsBadCredentials() {
        User inactiveUser = User.builder()
                .id(userId)
                .email("analyst@acme.com")
                .passwordHash(passwordEncoder.encode(rawPassword))
                .tenantId(tenantId)
                .roles(Set.of(UserRole.ANALYST))
                .status(UserStatus.INACTIVE)
                .build();

        when(userRepository.findByEmailAndTenantId("analyst@acme.com", tenantId))
                .thenReturn(Optional.of(inactiveUser));

        assertThatThrownBy(() -> authService.login("analyst@acme.com", rawPassword, tenantId))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("inactive");
    }

    @Test
    @DisplayName("refresh returns new tokens for valid refresh token")
    void refresh_validRefreshToken_returnsNewTokens() {
        Claims mockClaims = mock(Claims.class);
        when(tokenService.validateAndParseClaims("valid-refresh-token")).thenReturn(mockClaims);
        when(tokenService.isRefreshToken(mockClaims)).thenReturn(true);
        when(tokenService.extractUserId(mockClaims)).thenReturn(userId);
        when(tokenService.extractTenantId(mockClaims)).thenReturn(tenantId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(activeUser));
        when(tokenService.generateAccessToken(activeUser)).thenReturn("new-access-token");
        when(tokenService.generateRefreshToken(activeUser)).thenReturn("new-refresh-token");

        AuthResponse response = authService.refresh("valid-refresh-token");

        assertThat(response.getAccessToken()).isEqualTo("new-access-token");
        assertThat(response.getRefreshToken()).isEqualTo("new-refresh-token");
    }

    @Test
    @DisplayName("refresh throws BadCredentialsException when token is not a refresh token")
    void refresh_notRefreshToken_throwsBadCredentials() {
        Claims mockClaims = mock(Claims.class);
        when(tokenService.validateAndParseClaims("access-token")).thenReturn(mockClaims);
        when(tokenService.isRefreshToken(mockClaims)).thenReturn(false);

        assertThatThrownBy(() -> authService.refresh("access-token"))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("not a refresh token");
    }
}
