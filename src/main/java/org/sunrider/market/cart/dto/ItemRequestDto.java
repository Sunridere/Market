package org.sunrider.market.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import java.util.UUID;
import lombok.Builder;

@Builder
@Schema(description = "Запрос на добавление предмета")
public record ItemRequestDto(

    @Schema(description = "ID продукта")
    UUID productId,

    @Min(1)
    @Schema(description = "Количество продукта")
    Integer quantity

) {

}
