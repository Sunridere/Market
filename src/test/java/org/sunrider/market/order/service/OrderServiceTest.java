package org.sunrider.market.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sunrider.market.cart.dto.CartDto;
import org.sunrider.market.cart.dto.CartItemDto;
import org.sunrider.market.cart.service.CartService;
import org.sunrider.market.exception.BadRequestException;
import org.sunrider.market.exception.NotFoundException;
import org.sunrider.market.order.dto.OrderDto;
import org.sunrider.market.order.dto.OrderItemDto;
import org.sunrider.market.order.entity.Order;
import org.sunrider.market.order.entity.OrderStatus;
import org.sunrider.market.order.mapper.OrderMapper;
import org.sunrider.market.order.repository.OrderRepository;
import org.sunrider.market.product.dto.CategoryDto;
import org.sunrider.market.product.dto.ProductDto;
import org.sunrider.market.product.service.ProductService;
import org.sunrider.market.user.entity.Role;
import org.sunrider.market.user.entity.User;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductService productService;

    @Mock
    private CartService cartService;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private UUID userId;
    private UUID productId;
    private UUID orderId;
    private CategoryDto categoryDto;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        productId = UUID.randomUUID();
        orderId = UUID.randomUUID();

        user = User.builder()
            .id(userId)
            .username("testuser")
            .email("test@test.com")
            .password("encoded")
            .role(Role.ROLE_USER)
            .build();

        categoryDto = new CategoryDto(UUID.randomUUID(), "Электроника");
    }

    @Test
    void createOrder_success() {
        CartItemDto cartItemDto = new CartItemDto(
            UUID.randomUUID(), productId, "Iphone 13", 2,
            BigDecimal.valueOf(30000), BigDecimal.valueOf(60000));
        CartDto cartDto = new CartDto(UUID.randomUUID(), new ArrayList<>(List.of(cartItemDto)), BigDecimal.valueOf(60000));

        ProductDto productDto = new ProductDto(productId, "Iphone 13", "Описание",
            BigDecimal.valueOf(30000), 50, categoryDto);

        OrderDto orderDto = new OrderDto(orderId, OrderStatus.CREATED,
            List.of(new OrderItemDto(UUID.randomUUID(), productId, "Iphone 13",
                2, BigDecimal.valueOf(30000), BigDecimal.valueOf(60000))),
            BigDecimal.valueOf(60000));

        when(cartService.getCart(user)).thenReturn(cartDto);
        when(productService.getProducts(anySet())).thenReturn(List.of(productDto));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(orderId);
            return order;
        });
        when(orderMapper.toDto(any(Order.class))).thenReturn(orderDto);

        OrderDto result = orderService.createOrder(user);

        assertThat(result.status()).isEqualTo(OrderStatus.CREATED);
        assertThat(result.totalOrderPrice()).isEqualByComparingTo(BigDecimal.valueOf(60000));
        verify(cartService).clearCart(user);
    }

    @Test
    void createOrder_emptyCart_throws() {
        CartDto emptyCart = new CartDto(UUID.randomUUID(), new ArrayList<>(), BigDecimal.ZERO);
        when(cartService.getCart(user)).thenReturn(emptyCart);

        assertThatThrownBy(() -> orderService.createOrder(user))
            .isInstanceOf(BadRequestException.class)
            .hasMessage("Корзина пустая");
    }

    @Test
    void createOrder_productNotFound_throws() {
        UUID missingProductId = UUID.randomUUID();
        CartItemDto cartItemDto = new CartItemDto(
            UUID.randomUUID(), missingProductId, "Missing", 1,
            BigDecimal.valueOf(1000), BigDecimal.valueOf(1000));
        CartDto cartDto = new CartDto(UUID.randomUUID(), List.of(cartItemDto), BigDecimal.valueOf(1000));

        when(cartService.getCart(user)).thenReturn(cartDto);
        when(productService.getProducts(anySet())).thenReturn(List.of());

        assertThatThrownBy(() -> orderService.createOrder(user))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("Products not found");
    }

    @Test
    void createOrder_insufficientStock_throws() {
        CartItemDto cartItemDto = new CartItemDto(
            UUID.randomUUID(), productId, "Iphone 13", 100,
            BigDecimal.valueOf(30000), BigDecimal.valueOf(3000000));
        CartDto cartDto = new CartDto(UUID.randomUUID(), List.of(cartItemDto), BigDecimal.valueOf(3000000));

        ProductDto productDto = new ProductDto(productId, "Iphone 13", "Описание",
            BigDecimal.valueOf(30000), 5, categoryDto);

        when(cartService.getCart(user)).thenReturn(cartDto);
        when(productService.getProducts(anySet())).thenReturn(List.of(productDto));

        assertThatThrownBy(() -> orderService.createOrder(user))
            .isInstanceOf(BadRequestException.class)
            .hasMessageContaining("На складе недостаточно продукта");
    }

    @Test
    void getOrder_byUserId_success() {
        Order order = Order.builder()
            .id(orderId)
            .user(user)
            .status(OrderStatus.CREATED)
            .items(new ArrayList<>())
            .build();

        OrderDto orderDto = new OrderDto(orderId, OrderStatus.CREATED, List.of(), BigDecimal.ZERO);

        when(orderRepository.findByUserId(userId)).thenReturn(Optional.of(List.of(order)));
        when(orderMapper.toDto(List.of(order))).thenReturn(List.of(orderDto));

        List<OrderDto> result = orderService.getOrder(userId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).status()).isEqualTo(OrderStatus.CREATED);
    }

    @Test
    void getOrder_byUserId_notFound_throws() {
        when(orderRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrder(userId))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("У пользователя нет заказов.");
    }

    @Test
    void getOrder_byUserIdAndOrderId_success() {
        Order order = Order.builder()
            .id(orderId)
            .user(user)
            .status(OrderStatus.CREATED)
            .items(new ArrayList<>())
            .build();

        OrderDto orderDto = new OrderDto(orderId, OrderStatus.CREATED, List.of(), BigDecimal.ZERO);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderMapper.toDto(order)).thenReturn(orderDto);

        OrderDto result = orderService.getOrder(userId, orderId);

        assertThat(result.id()).isEqualTo(orderId);
    }

    @Test
    void getOrder_byUserIdAndOrderId_wrongUser_throws() {
        User otherUser = User.builder()
            .id(UUID.randomUUID())
            .username("other")
            .build();

        Order order = Order.builder()
            .id(orderId)
            .user(otherUser)
            .status(OrderStatus.CREATED)
            .items(new ArrayList<>())
            .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.getOrder(userId, orderId))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("Заказ не найден");
    }

    @Test
    void getOrder_byUserIdAndOrderId_notFound_throws() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrder(userId, orderId))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("Заказ не найден");
    }
}
