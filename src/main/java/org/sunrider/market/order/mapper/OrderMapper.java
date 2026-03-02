package org.sunrider.market.order.mapper;

import java.math.BigDecimal;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.sunrider.market.order.dto.OrderDto;
import org.sunrider.market.order.dto.OrderItemDto;
import org.sunrider.market.order.entity.Order;
import org.sunrider.market.order.entity.OrderItem;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {

    @Mapping(target = "totalOrderPrice", expression = "java(calculateTotal(order))")
    OrderDto toDto(Order order);

    List<OrderDto> toDto(List<Order> orders);

    @Mapping(target = "name", source = "product.name")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "totalPriceAtPurchase", expression = "java(calculateItemTotal(orderItem))")
    OrderItemDto toOrderItemDto(OrderItem orderItem);


    default BigDecimal calculateItemTotal(OrderItem orderItem) {
        return orderItem
            .getPriceAtPurchase()
            .multiply(new BigDecimal(orderItem.getQuantity()));
    }

    default BigDecimal calculateTotal(Order order) {
        if (order.getItems() == null) {
            return BigDecimal.ZERO;
        } return order.getItems().stream()
            .map(this::calculateItemTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
