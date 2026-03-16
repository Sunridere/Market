package org.sunrider.market.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import org.sunrider.market.order.entity.OrderStatus;

@Builder
@Schema(description = "Заказ")
public record OrderDto(

    @Schema(description = "ID заказа", example = "2b4d24b2-3686-448e-873d-75151d2faf8b")
    UUID id,

    @Schema(description = "Статус заказа", example = "CREATED")
    OrderStatus status,

    @Schema(description = "Список заказов")
    List<OrderItemDto> items,

    @Schema(description = "Общая цена заказа", example = "60000")
    BigDecimal totalOrderPrice,

    @Schema(description = "Дата создания заказа", example = "2025-07-10T14:25:30.123456")
    LocalDateTime createdAt,

    @Schema(description = "Дата обновления заказа", example = "2025-07-10T14:25:30.123456")
    LocalDateTime updatedAt

) {}
