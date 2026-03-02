package org.sunrider.market.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;

@Builder
@Schema(description = "Экземпляр продукта в заказе")
public record OrderItemDto(

    @Schema(description = "ID продукта в заказе")
    UUID id,

    @Schema(description = "ID продукта")
    UUID productId,

    @Schema(description = "Название продукта в заказе")
    String name,

    @Schema(description = "Количество экземпляров в заказе")
    Integer quantity,

    @Schema(description = "Цена товара на момент покупки")
    BigDecimal priceAtPurchase,

    @Schema(description = "Цена с учетом количества")
    BigDecimal totalPriceAtPurchase

) {

}
