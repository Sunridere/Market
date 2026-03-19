package org.sunrider.market.security.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.sunrider.market.exception.BadRequestException;
import org.sunrider.market.exception.NotFoundException;
import org.sunrider.market.security.entity.RefreshToken;
import org.sunrider.market.security.repository.RefreshTokenRepository;
import org.sunrider.market.user.entity.User;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public String createRefreshToken(User user) {

        UUID uuid = UUID.randomUUID();
        RefreshToken refreshToken = RefreshToken.builder()
            .user(user)
            .token(uuid.toString())
            .createdAt(LocalDateTime.now())
            .expiresAt(LocalDateTime.now().plusDays(7))
            .revoked(false)
            .build();

        return refreshTokenRepository.save(refreshToken).getToken();
    }

    public boolean validateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findRefreshTokenByToken(token)
            .orElseThrow(() -> new NotFoundException("Refresh token не найден"));

        return !refreshToken.getExpiresAt().isBefore(LocalDateTime.now())
            && !refreshToken.isRevoked();
    }

    public void revokeRefreshToken(String token) {
        refreshTokenRepository.findRefreshTokenByToken(token).ifPresent(refreshToken -> {
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
        });
    }

    public void revokeAllUserTokens(User user) {
        List<RefreshToken> refreshTokens = refreshTokenRepository.findRefreshTokenByUserId(user.getId());
        if (refreshTokens.isEmpty()) {
            return;
        }
        for (RefreshToken refreshToken : refreshTokens) {
            refreshToken.setRevoked(true);
        }
        refreshTokenRepository.saveAll(refreshTokens);
    }

    public User getUserByToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findRefreshTokenByToken(token)
            .orElseThrow(() -> new NotFoundException("Refresh token не найден"));
        if (!refreshToken.isRevoked() && !refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            return refreshToken.getUser();
        }
        throw new BadRequestException("Refresh token невалиден");
    }

}
