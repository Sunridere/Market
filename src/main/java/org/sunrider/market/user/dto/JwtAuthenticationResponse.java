package org.sunrider.market.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Ответ с токеном доступа")
public record JwtAuthenticationResponse(

    @Schema(description = "Токен доступа", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTYyMjUwNj...")
    String accessToken,

    @Schema(description = "Токен обновления", example = "9a93aa31-7687-4763-9a87-e0b173f4f5a5")
    String refreshToken
) {

}
