package org.sunrider.market.cart.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.sunrider.market.cart.dto.CartDto;
import org.sunrider.market.cart.dto.ItemRequestDto;
import org.sunrider.market.cart.service.CartService;
import org.sunrider.market.user.entity.User;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cart")
public class CartController {

    private final CartService cartService;

    @GetMapping
    public CartDto cart(@AuthenticationPrincipal User user) {
        return cartService.getCart(user.getId());
    }

    @PostMapping("/items")
    public CartDto addItem(@AuthenticationPrincipal User user, @RequestBody ItemRequestDto request) {
        return cartService.addItem(user.getId(), request);
    }
}
