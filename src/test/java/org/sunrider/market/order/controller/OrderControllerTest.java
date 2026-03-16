package org.sunrider.market.order.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;
import org.sunrider.market.exception.BadRequestException;
import org.sunrider.market.exception.NotFoundException;
import org.sunrider.market.order.dto.OrderDto;
import org.sunrider.market.order.dto.OrderItemDto;
import org.sunrider.market.order.entity.OrderStatus;
import org.sunrider.market.order.service.OrderService;
import org.sunrider.market.security.JwtService;
import org.sunrider.market.user.entity.Role;
import org.sunrider.market.user.entity.User;
import org.sunrider.market.user.service.UserService;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserService userService;

    private User testUser;
    private OrderDto orderDto;
    private UUID orderId;
    private UUID userId;
    private int page = 0, size = 10;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        orderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        testUser = User.builder()
            .id(userId)
            .username("testuser")
            .email("test@test.com")
            .password("encoded")
            .role(Role.ROLE_USER)
            .build();

        OrderItemDto orderItemDto = new OrderItemDto(
            UUID.randomUUID(), productId, "Iphone 13",
            2, BigDecimal.valueOf(30000), BigDecimal.valueOf(60000));
        orderDto = new OrderDto(orderId, OrderStatus.CREATED,
            List.of(orderItemDto), BigDecimal.valueOf(60000), LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void createOrder_success() throws Exception {
        when(orderService.createOrder(any(User.class))).thenReturn(orderDto);

        mockMvc.perform(post("/api/v1/orders")
                .with(user(testUser))
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("CREATED"))
            .andExpect(jsonPath("$.totalOrderPrice").value(60000));
    }

    @Test
    void createOrder_emptyCart_returnsBadRequest() throws Exception {
        when(orderService.createOrder(any(User.class)))
            .thenThrow(new BadRequestException("Корзина пустая"));

        mockMvc.perform(post("/api/v1/orders")
                .with(user(testUser))
                .with(csrf()))
            .andExpect(status().isBadRequest());
    }

    @Test
    void getOrders_success() throws Exception {
        when(orderService.getOrder(userId, page, size)).thenReturn(new PageImpl<>(Collections.singletonList(orderDto)));

        mockMvc.perform(get("/api/v1/orders")
                .with(user(testUser)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].status").value("CREATED"));
    }

    @Test
    void getOrders_notFound() throws Exception {
        when(orderService.getOrder(userId, page, size))
            .thenThrow(new NotFoundException("У пользователя нет заказов."));

        mockMvc.perform(get("/api/v1/orders")
                .with(user(testUser)))
            .andExpect(status().isNotFound());
    }

    @Test
    void getOrderById_success() throws Exception {
        when(orderService.getOrder(userId, orderId)).thenReturn(orderDto);

        mockMvc.perform(get("/api/v1/orders/{orderId}", orderId)
                .with(user(testUser)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(orderId.toString()));
    }

    @Test
    void getOrderById_notFound() throws Exception {
        when(orderService.getOrder(eq(userId), any(UUID.class)))
            .thenThrow(new NotFoundException("Заказ не найден"));

        mockMvc.perform(get("/api/v1/orders/{orderId}", UUID.randomUUID())
                .with(user(testUser)))
            .andExpect(status().isNotFound());
    }
}
