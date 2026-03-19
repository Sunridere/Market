package org.sunrider.market.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
@Schema(description = "Запрос Refresh Token")
public record RefreshTokenRequest(
    @Schema(description = "Refresh token", example = "9a93aa31-7687-4763-9a87-e0b173f4f5a5")
    @NotBlank
    String refreshToken
) {

}
