package org.sunrider.market.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
@Schema(description = "Информация о продукте")
public record ProductDto (

    @Schema(description = "ID продукта", example = "2b4d24b2-3686-448e-873d-75151d2faf8b")
    UUID id,

    @Schema(description = "Название продукта", example = "Iphone 13 PRO")
    @NotBlank
    String name,

    @Schema(description = "Описание продукта", example = "Самый новый смартфон от Apple...")
    @NotBlank
    String description,

    @Schema(description = "Цена продукта", example = "30000")
    @NotNull
    @Positive
    BigDecimal price,

    @Schema(description = "Количество на складе", example = "50")
    @NotNull
    @Positive
    Integer stockQuantity,

    @Schema(description = "Категория продукта", example = "Смартфон")
    @NotNull
    CategoryDto category,

    @Schema(description = "Изображения продукта")
    List<ProductImageDto> images,

    @Schema(description = "Дата создания продукта", example = "2025-07-10T14:25:30.123456")
    LocalDateTime createdAt,

    @Schema(description = "Дата обновления продукта", example = "2025-07-10T14:25:30.123456")
    LocalDateTime updatedAt

) { }
