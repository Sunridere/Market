package org.sunrider.market.cart.service;

import java.util.ArrayList;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;
import org.sunrider.market.cart.dto.CartDto;
import org.sunrider.market.cart.dto.ItemRequestDto;
import org.sunrider.market.cart.entity.Cart;
import org.sunrider.market.cart.entity.CartItem;
import org.sunrider.market.cart.mapper.CartMapper;
import org.sunrider.market.cart.repository.CartRepository;
import org.sunrider.market.product.entity.Product;
import org.sunrider.market.product.repository.ProductRepository;
import org.sunrider.market.user.entity.User;

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

    @SneakyThrows
    public CartDto addItem(UUID id, ItemRequestDto request) {
        Cart cart = cartRepository.findByUserId(id)
            .orElseGet(() -> {
                return Cart.builder()
                    .user(User.builder().id(id).build())
                    .items(new ArrayList<>())
                    .build();
            });

        Product product = productRepository.findById(request.productId()).orElseThrow(NotFoundException::new);

        cart.getItems().add(CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(request.quantity())
                .build());
        return cartMapper.cartToCartDto(cartRepository.save(cart));
    }

}
