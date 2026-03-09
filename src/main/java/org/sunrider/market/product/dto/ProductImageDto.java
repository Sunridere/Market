package org.sunrider.market.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.Builder;

@Builder
@Schema(description = "Изображение продукта")
public record ProductImageDto(

    @Schema(description = "ID изображения")
    UUID id,

    @Schema(description = "URL изображения", example = "https://example.com/image.jpg")
    String imageUrl,

    @Schema(description = "Главное изображение", example = "true")
    Boolean isMain

) { }
