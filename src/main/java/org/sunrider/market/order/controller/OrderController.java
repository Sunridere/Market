package org.sunrider.market.order.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.sunrider.market.order.dto.OrderDto;
import org.sunrider.market.order.service.OrderService;
import org.sunrider.market.user.entity.User;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public OrderDto createOrder(@AuthenticationPrincipal User user) {
        return orderService.createOrder(user);
    }

    @GetMapping
    public List<OrderDto> getOrder(@AuthenticationPrincipal User user) {
        return orderService.getOrder(user.getId());
    }

}
