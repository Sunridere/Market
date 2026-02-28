package org.sunrider.market.cart.mapper;

import java.math.BigDecimal;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.sunrider.market.cart.dto.CartDto;
import org.sunrider.market.cart.dto.CartItemDto;
import org.sunrider.market.cart.entity.Cart;
import org.sunrider.market.cart.entity.CartItem;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CartMapper {

    @Mapping(target = "totalCartPrice", expression = "java(calculateTotal(cart))")
    CartDto cartToCartDto(Cart cart); @Mapping(target = "productId", source = "product.id")

    @Mapping(target = "name", source = "product.name")
    @Mapping(target = "unitPrice", source = "product.price")
    @Mapping(target = "totalPrice", expression = "java(calculateItemTotal(cartItem))")
    CartItemDto cartItemToCartItemDto(CartItem cartItem);

    List<CartItemDto> cartItemToCartItemDtoList(List<CartItem> cartItemList);

    default BigDecimal calculateItemTotal(CartItem cartItem) {
        if (cartItem.getProduct() == null || cartItem.getProduct().getPrice() == null) {
            return BigDecimal.ZERO;
        }
        return cartItem
            .getProduct()
            .getPrice()
            .multiply(new BigDecimal(cartItem.getQuantity()));
    }

    default BigDecimal calculateTotal(Cart cart) {
        if (cart.getItems() == null) {
            return BigDecimal.ZERO;
        } return cart.getItems().stream()
            .map(this::calculateItemTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}