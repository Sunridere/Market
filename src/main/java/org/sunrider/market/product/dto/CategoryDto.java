package org.sunrider.market.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.Builder;

@Builder
@Schema(description = "Категория товара")
public record CategoryDto(

    @Schema(description = "ID категории", example = "10000000-0000-0000-0000-000000000001")
    UUID id,

    @Schema(description = "Название категории", example = "Техника")
    String name
) {

}
