package org.sunrider.market.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
@Schema(description = "Корзина")
public record CartDto(

    @Schema(description = "ID корзины", example = "2b4d24b2-3686-448e-873d-75151d2faf8b")
    UUID id,

    @Schema(description = "Список предметов в корзине")
    List<CartItemDto> items,

    @Schema(description = "Итоговая цена корзины", example = "60000")
    BigDecimal totalCartPrice

) {}
