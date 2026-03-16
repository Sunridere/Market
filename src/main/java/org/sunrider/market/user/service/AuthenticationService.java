package org.sunrider.market.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.sunrider.market.security.JwtService;
import org.sunrider.market.security.LoginRateLimiterService;
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

    public JwtAuthenticationResponse signUp(SignUpRequest signUpRequest ) {

        var user = User.builder()
            .username(signUpRequest.username())
            .password(passwordEncoder.encode(signUpRequest.password()))
            .email(signUpRequest.email())
            .firstName(signUpRequest.firstName())
            .lastName(signUpRequest.lastName())
            .role(Role.ROLE_USER)
            .build();

        userService.createUser(user);

        var token = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(token);

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

        var user = userService
            .userDetailsService()
            .loadUserByUsername(signInRequest.username());

        var token = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(token);

    }

}
