package org.sunrider.market.cart.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Сервис корзины")
@RequestMapping("/api/v1/cart")
public class CartController {

    private final CartService cartService;

    @Operation(summary = "Получение корзины пользователя")
    @GetMapping
    public CartDto getCart(@AuthenticationPrincipal User user) {
        return cartService.getCart(user);
    }

    @Operation(summary = "Добавление товара в корзину")
    @PostMapping("/items")
    public CartDto addItem(
        @AuthenticationPrincipal User user,
        @Valid @RequestBody ItemRequestDto request) {
        return cartService.addItem(user, request);
    }

    @Operation(summary = "Изменение количества товара в корзине")
    @PutMapping("/items/{productId}")
    public CartDto updateItemQuantity(
        @AuthenticationPrincipal User user,
        @PathVariable UUID productId,
        @Valid @RequestBody UpdateQuantityRequestDto request
    ) {
        return cartService.updateItemQuantity(user, productId, request);
    }

    @Operation(summary = "Удаление товара из корзины")
    @DeleteMapping("/items/{productId}")
    public CartDto removeItem(
        @AuthenticationPrincipal User user,
        @PathVariable UUID productId
    ) {
        return cartService.removeItem(user, productId);
    }

    @Operation(summary = "Удаление всех товаров из корзины")
    @DeleteMapping("/clear")
    public CartDto clearCart(@AuthenticationPrincipal User user) {
        return cartService.clearCart(user);
    }
}
