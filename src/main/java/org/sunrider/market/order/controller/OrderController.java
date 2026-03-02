package org.sunrider.market.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.sunrider.market.order.dto.OrderDto;
import org.sunrider.market.order.service.OrderService;
import org.sunrider.market.user.entity.User;

@RestController
@RequiredArgsConstructor
@Tag(name = "Сервис заказов")
@RequestMapping("/api/v1/order")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Создание заказа")
    @PostMapping
    public OrderDto createOrder(@AuthenticationPrincipal User user) {
        return orderService.createOrder(user);
    }

    @Operation(summary = "Получение списка заказов пользователя")
    @GetMapping
    public List<OrderDto> getOrder(@AuthenticationPrincipal User user) {
        return orderService.getOrder(user.getId());
    }

    @Operation(summary = "Получение информации по заказу")
    @GetMapping("/{orderId}")
    public OrderDto getOrderById(@AuthenticationPrincipal User user, @PathVariable UUID orderId) {
        return orderService.getOrder(user.getId(), orderId);
    }
}
