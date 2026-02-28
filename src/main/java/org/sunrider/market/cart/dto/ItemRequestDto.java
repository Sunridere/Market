package org.sunrider.market.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.Builder;

@Builder
@Schema(description = "Запрос на добавление предмета")
public record ItemRequestDto(

    @Schema(description = "ID продукта")
    UUID productId,

    @Schema(description = "Количество продукта")
    Integer quantity

) {

}
