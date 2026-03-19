package org.sunrider.market.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.sunrider.market.user.dto.JwtAuthenticationResponse;
import org.sunrider.market.user.dto.RefreshTokenRequest;
import org.sunrider.market.user.dto.SignInRequest;
import org.sunrider.market.user.dto.SignUpRequest;
import org.sunrider.market.user.service.AuthenticationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Аутентификация")
public class AuthController {

    private final AuthenticationService authenticationService;

    @Operation(summary = "Регистрация пользователя")
    @PostMapping("/sign-up")
    public JwtAuthenticationResponse signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        return authenticationService.signUp(signUpRequest);
    }

    @Operation(summary = "Авторизация пользователя")
    @PostMapping("/sign-in")
    public JwtAuthenticationResponse signIn(
        @Valid @RequestBody SignInRequest signInRequest,
        HttpServletRequest request
    ) {
        String ip = extractIp(request);
        return authenticationService.signIn(signInRequest, ip);
    }

    private String extractIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    @Operation(summary = "Обновление токена")
    @PostMapping("/refresh")
    public JwtAuthenticationResponse refresh(@RequestBody RefreshTokenRequest request) {
        return authenticationService.refreshToken(request.refreshToken());
    }

    @Operation(summary = "Выйти из аккаунта")
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestBody RefreshTokenRequest request) {
        authenticationService.logout(request.refreshToken());
    }
}
