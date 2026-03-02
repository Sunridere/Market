package org.sunrider.market.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Builder;

@Builder
@Schema(description = "Запрос на добавление предмета")
public record ItemRequestDto(

    @Schema(description = "ID продукта", example = "2b4d24b2-3686-448e-873d-75151d2faf8b")
    @NotNull(message = "ID не должно быть null")
    UUID productId,

    @Min(value = 1, message = "Количество должно быть 1 или больше")
    @Schema(description = "Количество продукта", example = "3")
    Integer quantity

) {

}
