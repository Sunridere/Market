package org.sunrider.market.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.sunrider.market.exception.BadRequestException;
import org.sunrider.market.security.service.JwtService;
import org.sunrider.market.security.service.LoginRateLimiterService;
import org.sunrider.market.security.service.RefreshTokenService;
import org.sunrider.market.user.dto.JwtAuthenticationResponse;
import org.sunrider.market.user.dto.SignInRequest;
import org.sunrider.market.user.dto.SignUpRequest;
import org.sunrider.market.user.entity.Role;
import org.sunrider.market.user.entity.User;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final LoginRateLimiterService rateLimiterService;
    private final RefreshTokenService refreshTokenService;

    public JwtAuthenticationResponse signUp(SignUpRequest signUpRequest ) {

        User user = User.builder()
            .username(signUpRequest.username())
            .password(passwordEncoder.encode(signUpRequest.password()))
            .email(signUpRequest.email())
            .firstName(signUpRequest.firstName())
            .lastName(signUpRequest.lastName())
            .role(Role.ROLE_USER)
            .build();

        userService.createUser(user);

        String accessToken = jwtService.generateToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user);
        return new JwtAuthenticationResponse(accessToken, refreshToken);
    }

    public JwtAuthenticationResponse signIn(SignInRequest signInRequest, String ip) {

        rateLimiterService.checkLimit(ip);

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                signInRequest.username(), signInRequest.password()
            ));
        } catch (BadCredentialsException e) {
            rateLimiterService.recordFailure(ip);
            throw e;
        }

        rateLimiterService.recordSuccess(ip);

        UserDetails userDetails = userService
            .userDetailsService()
            .loadUserByUsername(signInRequest.username());

        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken;
        if (userDetails instanceof User user) {
            refreshToken = refreshTokenService.createRefreshToken(user);
            return new JwtAuthenticationResponse(accessToken, refreshToken);
        }

        throw  new BadRequestException("Не удалось сгенерировать токены");
    }

    public JwtAuthenticationResponse refreshToken(String refreshToken) {
        User user = refreshTokenService.getUserByToken(refreshToken);
        refreshTokenService.revokeRefreshToken(refreshToken);
        String accessToken = jwtService.generateToken(user);
        refreshToken = refreshTokenService.createRefreshToken(user);
        return new JwtAuthenticationResponse(accessToken, refreshToken);

    }

    public void logout(String refreshToken) {
        refreshTokenService.revokeRefreshToken(refreshToken);
    }

}
