package org.sunrider.market.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Запрос на обновление количества товара в корзине")
public record UpdateQuantityRequestDto(

    @NotNull
    @Min(0)
    @Schema(description = "Новое количество товара (0 — удалить из корзины)")
    Integer quantity

) {
}
