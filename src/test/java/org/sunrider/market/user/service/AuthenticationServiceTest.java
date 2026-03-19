package org.sunrider.market.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.sunrider.market.security.service.JwtService;
import org.sunrider.market.security.service.LoginRateLimiterService;
import org.sunrider.market.security.service.RefreshTokenService;
import org.sunrider.market.user.dto.JwtAuthenticationResponse;
import org.sunrider.market.user.dto.SignInRequest;
import org.sunrider.market.user.dto.SignUpRequest;
import org.sunrider.market.user.entity.Role;
import org.sunrider.market.user.entity.User;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private LoginRateLimiterService loginRateLimiterService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
            .username("testuser")
            .email("test@test.com")
            .password("encodedPassword")
            .firstName("Test")
            .lastName("User")
            .role(Role.ROLE_USER)
            .build();
    }

    @Test
    void signUp_success() {
        SignUpRequest request = new SignUpRequest("testuser", "test@test.com", "password123", "Test", "User");

        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userService.createUser(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("jwt-token");
        when(refreshTokenService.createRefreshToken(any(User.class))).thenReturn("refresh-token");

        JwtAuthenticationResponse response = authenticationService.signUp(request);

        assertThat(response.accessToken()).isEqualTo("jwt-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-token");
        verify(userService).createUser(any(User.class));
        verify(jwtService).generateToken(any(UserDetails.class));
        verify(refreshTokenService).createRefreshToken(any(User.class));
    }

    @Test
    void signIn_success() {
        SignInRequest request = new SignInRequest("testuser", "password123");

        UserDetailsService userDetailsService = username -> user;
        when(userService.userDetailsService()).thenReturn(userDetailsService);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("jwt-token");
        when(refreshTokenService.createRefreshToken(any(User.class))).thenReturn("refresh-token");
        doNothing().when(loginRateLimiterService).checkLimit(any(String.class));

        JwtAuthenticationResponse response = authenticationService.signIn(request, "192.168.1.1");

        assertThat(response.accessToken()).isEqualTo("jwt-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-token");
        verify(authenticationManager).authenticate(any());
        verify(jwtService).generateToken(any(UserDetails.class));
        verify(refreshTokenService).createRefreshToken(any(User.class));
    }

    @Test
    void refreshToken_success() {
        when(refreshTokenService.getUserByToken("old-refresh-token")).thenReturn(user);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("new-jwt-token");
        when(refreshTokenService.createRefreshToken(user)).thenReturn("new-refresh-token");

        JwtAuthenticationResponse response = authenticationService.refreshToken("old-refresh-token");

        assertThat(response.accessToken()).isEqualTo("new-jwt-token");
        assertThat(response.refreshToken()).isEqualTo("new-refresh-token");
        verify(refreshTokenService).revokeRefreshToken("old-refresh-token");
        verify(refreshTokenService).createRefreshToken(user);
    }

    @Test
    void logout_success() {
        authenticationService.logout("refresh-token");

        verify(refreshTokenService).revokeRefreshToken("refresh-token");
    }
}
