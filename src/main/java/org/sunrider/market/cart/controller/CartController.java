package org.sunrider.market.cart.controller;

import java.util.UUID;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.sunrider.market.cart.dto.CartDto;
import org.sunrider.market.cart.dto.ItemRequestDto;
import org.sunrider.market.cart.dto.UpdateQuantityRequestDto;
import org.sunrider.market.cart.service.CartService;
import org.sunrider.market.user.entity.User;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cart")
public class CartController {

    private final CartService cartService;

    @GetMapping
    public CartDto cart(@AuthenticationPrincipal User user) {
        return cartService.getCart(user);
    }

    @PostMapping("/items")
    public CartDto addItem(
        @AuthenticationPrincipal User user,
        @Valid @RequestBody ItemRequestDto request) {
        return cartService.addItem(user, request);
    }

    @PutMapping("/items/{productId}")
    public CartDto updateItemQuantity(
        @AuthenticationPrincipal User user,
        @PathVariable UUID productId,
        @Valid @RequestBody UpdateQuantityRequestDto request
    ) {
        return cartService.updateItemQuantity(user, productId, request);
    }

    @DeleteMapping("/items/{productId}")
    public CartDto removeItem(
        @AuthenticationPrincipal User user,
        @PathVariable UUID productId
    ) {
        return cartService.removeItem(user, productId);
    }

    @DeleteMapping("/clear")
    public CartDto clearCart(@AuthenticationPrincipal User user) {
        return cartService.clearCart(user);
    }
}
