package org.sunrider.market.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;

@Builder
@Schema(description = "Экземпляр продукта в заказе")
public record OrderItemDto(

    @Schema(description = "ID продукта в заказе", example = "2b4d24b2-3686-448e-873d-75151d2faf8b")
    UUID id,

    @Schema(description = "ID продукта", example = "10000000-0000-0000-0000-000000000004")
    UUID productId,

    @Schema(description = "Название продукта в заказе", example = "Iphone 13 PRO")
    String name,

    @Schema(description = "Количество экземпляров в заказе", example = "2")
    Integer quantity,

    @Schema(description = "Цена товара на момент покупки", example = "30000")
    BigDecimal priceAtPurchase,

    @Schema(description = "Цена с учетом количества", example = "60000")
    BigDecimal totalPriceAtPurchase

) {

}
