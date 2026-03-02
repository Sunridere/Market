package org.sunrider.market.order.service;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.sunrider.market.cart.dto.CartDto;
import org.sunrider.market.cart.dto.CartItemDto;
import org.sunrider.market.cart.service.CartService;
import org.sunrider.market.exception.BadRequestException;
import org.sunrider.market.exception.NotFoundException;
import org.sunrider.market.order.dto.OrderDto;
import org.sunrider.market.order.entity.Order;
import org.sunrider.market.order.entity.OrderItem;
import org.sunrider.market.order.entity.OrderStatus;
import org.sunrider.market.order.mapper.OrderMapper;
import org.sunrider.market.order.repository.OrderRepository;
import org.sunrider.market.product.dto.ProductDto;
import org.sunrider.market.product.entity.Category;
import org.sunrider.market.product.entity.Product;
import org.sunrider.market.product.service.ProductService;
import org.sunrider.market.user.entity.User;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final CartService cartService;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderDto createOrder(User user) {

        CartDto cart = cartService.getCart(user);

        if (cart.items().isEmpty()) {
            throw new BadRequestException("Корзина пустая");
        }

        Set<UUID> productIds = cart.items().stream()
            .map(CartItemDto::productId)
            .collect(Collectors.toSet());

        List<ProductDto> products = productService.getProducts(productIds);

        if (products.size() != productIds.size()) {
            Set<UUID> foundIds = products.stream()
                .map(ProductDto::id)
                .collect(Collectors.toSet());

            Set<UUID> missingIds = new HashSet<>(productIds);
            missingIds.removeAll(foundIds);
            throw new NotFoundException("Products not found: " + missingIds);
        }

        Map<UUID, ProductDto> productMap = products.stream().collect(Collectors.toMap(ProductDto::id, p -> p));
        Order order = Order.builder()
            .user(user)
            .status(OrderStatus.CREATED)
            .items(new ArrayList<>())
            .build();

        for (CartItemDto item : cart.items()) {

            ProductDto productDto = productMap.get(item.productId());

            if (productDto.stockQuantity() < item.quantity()) {
                throw new BadRequestException("На складе недостаточно продукта: " + productDto.name());
            }

            Product product = Product.builder()
                .id(productDto.id())
                .name(productDto.name())
                .description(productDto.description())
                .price(productDto.price())
                .stockQuantity(productDto.stockQuantity() - item.quantity())
                .category(Category.builder()
                    .id(productDto.category().id())
                    .name(productDto.category().name())
                    .build())
                .build();

            order.getItems().add(OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(item.quantity())
                    .priceAtPurchase(product.getPrice())
                    .build());
        }
        cart.items().clear();
        cartService.clearCart(user);
        return orderMapper.toDto(orderRepository.save(order));
    }

    public List<OrderDto> getOrder(UUID userID) {
        return orderMapper.toDto(orderRepository.findByUserId(userID)
            .orElseThrow(() -> new NotFoundException("У пользователя нет заказов.")));
    }



}
