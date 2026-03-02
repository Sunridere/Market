package org.sunrider.market.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;

@Builder
@Schema(description = "Предмет в корзине")
public record CartItemDto(

    @Schema(description = "Id продукта в корзине", example = "2b4d24b2-3686-448e-873d-75151d2faf8b")
    UUID id,

    @Schema(description = "Id продукта", example = "10000000-0000-0000-0000-000000000004")
    UUID productId,

    @Schema(description = "Название продукта в корзине", example = "Iphone 13 PRO")
    String name,

    @Schema(description = "Количество продукта в корзине", example = "2")
    Integer quantity,

    @Schema(description = "Цена продукта за штуку", example = "30000")
    BigDecimal unitPrice,

    @Schema(description = "Цена с учетом количества", example = "60000")
    BigDecimal totalPrice

) {}
