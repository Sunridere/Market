package org.sunrider.market.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;
import org.sunrider.market.product.entity.Category;

@Builder
@Schema(description = "Информация о продукте")
public record ProductDto (

    @Schema(description = "ID продукта", example = "2b4d24b2-3686-448e-873d-75151d2faf8b")
    UUID id,

    @Schema(description = "Название продукта", example = "Iphone 13 PRO")
    String name,

    @Schema(description = "Описание продукта", example = "Самый новый смартфон от Apple...")
    String description,

    @Schema(description = "Цена продукта", example = "30000")
    BigDecimal price,

    @Schema(description = "Количество на складе", example = "50")
    Integer stockQuantity,

    @Schema(description = "Категория продукта", example = "Смартфон")
    Category category

) { }
