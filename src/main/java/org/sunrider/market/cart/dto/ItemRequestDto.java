package org.sunrider.market.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import java.util.UUID;
import lombok.Builder;

@Builder
@Schema(description = "Запрос на добавление предмета")
public record ItemRequestDto(

    @Schema(description = "ID продукта", example = "2b4d24b2-3686-448e-873d-75151d2faf8b")
    UUID productId,

    @Min(1)
    @Schema(description = "Количество продукта", example = "3")
    Integer quantity

) {

}
