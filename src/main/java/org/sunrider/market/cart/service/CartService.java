package org.sunrider.market.cart.service;

import java.util.ArrayList;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sunrider.market.cart.dto.CartDto;
import org.sunrider.market.cart.dto.ItemRequestDto;
import org.sunrider.market.cart.dto.UpdateQuantityRequestDto;
import org.sunrider.market.cart.entity.Cart;
import org.sunrider.market.cart.entity.CartItem;
import org.sunrider.market.cart.mapper.CartMapper;
import org.sunrider.market.cart.repository.CartRepository;
import org.sunrider.market.exception.ItemAlreadyInCartException;
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

    public CartDto getCart(User user) {
        return cartMapper.cartToCartDto(cartRepository.findByUserId(user.getId()).orElseGet(() -> {
            Cart newCart = Cart.builder()
                .user(user)
                .items(new ArrayList<>())
                .build();
            return cartRepository.save(newCart);
        }));
    }

    @Transactional
    public CartDto addItem(User user, ItemRequestDto request) {
        Cart cart = cartRepository.findByUserId(user.getId())
            .orElseGet(() -> Cart.builder()
                .user(user)
                .items(new ArrayList<>())
                .build());

        Product product = productRepository.findById(request.productId())
            .orElseThrow(() -> new NotFoundException("Товар не найден"));

        cart.getItems().stream()
            .map(c -> c.getProduct().getId())
            .filter(id -> id.equals(request.productId()))
            .findFirst()
            .ifPresent(id -> {
                throw new ItemAlreadyInCartException("Товар уже в корзине");
            });

        cart.getItems().add(CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(request.quantity())
                .build());
        return cartMapper.cartToCartDto(cartRepository.save(cart));
    }

    @Transactional
    public CartDto updateItemQuantity(User user, UUID productId, UpdateQuantityRequestDto request) {
        Cart cart = cartRepository.findByUserId(user.getId())
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
    public CartDto removeItem(User user, UUID productId) {
        Cart cart = cartRepository.findByUserId(user.getId())
            .orElseThrow(() -> new NotFoundException("Корзина не найдена"));

        CartItem cartItem = cart.getItems().stream()
            .filter(item -> item.getProduct().getId().equals(productId))
            .findFirst()
            .orElseThrow(() -> new NotFoundException("Товар не найден в корзине"));

        cart.getItems().remove(cartItem);

        return cartMapper.cartToCartDto(cartRepository.save(cart));
    }

    @Transactional
    public CartDto clearCart(User user) {
        Cart cart = cartRepository.findByUserId(user.getId())
            .orElseGet(() -> {
                Cart newCart = Cart.builder()
                    .user(user)
                    .items(new ArrayList<>())
                    .build();
                return cartRepository.save(newCart);
            });

        cart.getItems().clear();

        return cartMapper.cartToCartDto(cartRepository.save(cart));
    }
}
