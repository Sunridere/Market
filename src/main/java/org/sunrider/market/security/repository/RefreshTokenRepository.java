package org.sunrider.market.security.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.sunrider.market.security.entity.RefreshToken;
import org.sunrider.market.user.entity.User;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findRefreshTokenByToken(String token);

    List<RefreshToken> findRefreshTokenByUserId(UUID userId);
}
