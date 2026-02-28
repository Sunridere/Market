package org.sunrider.market.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;

@Builder
@Schema(description = "Предмет в корзине")
public record CartItemDto(

    @Schema(description = "Id продукта в корзине")
    UUID id,

    @Schema(description = "Id продукта")
    UUID productId,

    @Schema(description = "Название продукта в корзине")
    String name,

    @Schema(description = "Количество продукта в корзине")
    Integer quantity,

    @Schema(description = "Цена продукта за штуку")
    BigDecimal unitPrice,

    @Schema(description = "Цена с учетом количества")
    BigDecimal totalPrice

) {}
