package org.sunrider.market.cart.service;

import java.util.ArrayList;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sunrider.market.cart.dto.CartDto;
import org.sunrider.market.cart.dto.ItemRequestDto;
import org.sunrider.market.cart.dto.UpdateQuantityRequestDto;
import org.sunrider.market.cart.entity.Cart;
import org.sunrider.market.cart.entity.CartItem;
import org.sunrider.market.cart.mapper.CartMapper;
import org.sunrider.market.cart.repository.CartRepository;
import org.sunrider.market.product.entity.Product;
import org.sunrider.market.product.repository.ProductRepository;
import org.sunrider.market.user.entity.User;
import org.sunrider.market.exception.NotFoundException;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;

    public CartDto getCart(UUID id) {
        return cartMapper.cartToCartDto(cartRepository.findByUserId(id).orElseGet(() -> {
            Cart newCart = Cart.builder().build();
            return cartRepository.save(newCart);
        }));
    }

    @Transactional
    public CartDto addItem(UUID id, ItemRequestDto request) {
        Cart cart = cartRepository.findByUserId(id)
            .orElseGet(() -> Cart.builder()
                .user(User.builder().id(id).build())
                .items(new ArrayList<>())
                .build());

        Product product = productRepository.findById(request.productId())
            .orElseThrow(() -> new NotFoundException("Продукт не найден"));

        cart.getItems().add(CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(request.quantity())
                .build());
        return cartMapper.cartToCartDto(cartRepository.save(cart));
    }

    @Transactional
    public CartDto updateItemQuantity(UUID userId, UUID productId, UpdateQuantityRequestDto request) {
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new NotFoundException("Корзина не найдена"));

        CartItem cartItem = cart.getItems().stream()
            .filter(item -> item.getProduct().getId().equals(productId))
            .findFirst()
            .orElseThrow(() -> new NotFoundException("Товар не найден в корзине"));

        if (request.quantity() == 0) {
            cart.getItems().remove(cartItem);
        } else {
            cartItem.setQuantity(request.quantity());
        }

        return cartMapper.cartToCartDto(cartRepository.save(cart));
    }

    @Transactional
    public CartDto removeItem(UUID userId, UUID productId) {
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new NotFoundException("Корзина не найдена"));

        CartItem cartItem = cart.getItems().stream()
            .filter(item -> item.getProduct().getId().equals(productId))
            .findFirst()
            .orElseThrow(() -> new NotFoundException("Товар не найден в корзине"));

        cart.getItems().remove(cartItem);

        return cartMapper.cartToCartDto(cartRepository.save(cart));
    }

    @Transactional
    public CartDto clearCart(UUID userId) {
        Cart cart = cartRepository.findByUserId(userId)
            .orElseGet(() -> {
                Cart newCart = Cart.builder()
                    .user(User.builder().id(userId).build())
                    .items(new ArrayList<>())
                    .build();
                return cartRepository.save(newCart);
            });

        cart.getItems().clear();

        return cartMapper.cartToCartDto(cartRepository.save(cart));
    }
}
