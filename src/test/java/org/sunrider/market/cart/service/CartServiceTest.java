package org.sunrider.market.cart.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
import org.sunrider.market.cart.dto.ItemRequestDto;
import org.sunrider.market.cart.dto.UpdateQuantityRequestDto;
import org.sunrider.market.cart.entity.Cart;
import org.sunrider.market.cart.entity.CartItem;
import org.sunrider.market.cart.mapper.CartMapper;
import org.sunrider.market.cart.repository.CartRepository;
import org.sunrider.market.exception.ItemAlreadyInCartException;
import org.sunrider.market.exception.NotFoundException;
import org.sunrider.market.product.entity.Product;
import org.sunrider.market.product.repository.ProductRepository;
import org.sunrider.market.user.entity.Role;
import org.sunrider.market.user.entity.User;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartMapper cartMapper;

    @InjectMocks
    private CartService cartService;

    private User user;
    private Cart cart;
    private Product product;
    private CartDto cartDto;
    private UUID userId;
    private UUID productId;
    private UUID cartId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        productId = UUID.randomUUID();
        cartId = UUID.randomUUID();

        user = User.builder()
            .id(userId)
            .username("testuser")
            .email("test@test.com")
            .password("encoded")
            .role(Role.ROLE_USER)
            .build();

        product = Product.builder()
            .id(productId)
            .name("Iphone 13")
            .price(BigDecimal.valueOf(30000))
            .stockQuantity(50)
            .build();

        cart = Cart.builder()
            .id(cartId)
            .user(user)
            .items(new ArrayList<>())
            .build();

        cartDto = new CartDto(cartId, List.of(), BigDecimal.ZERO);
    }

    @Test
    void getCart_existing() {
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartMapper.cartToCartDto(cart)).thenReturn(cartDto);

        CartDto result = cartService.getCart(user);

        assertThat(result).isEqualTo(cartDto);
    }

    @Test
    void getCart_createNew() {
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(cartMapper.cartToCartDto(cart)).thenReturn(cartDto);

        CartDto result = cartService.getCart(user);

        assertThat(result).isEqualTo(cartDto);
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void addItem_success() {
        ItemRequestDto request = new ItemRequestDto(productId, 2);

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(cartMapper.cartToCartDto(any(Cart.class))).thenReturn(cartDto);

        CartDto result = cartService.addItem(user, request);

        assertThat(result).isEqualTo(cartDto);
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void addItem_productNotFound_throws() {
        ItemRequestDto request = new ItemRequestDto(productId, 2);

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.addItem(user, request))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("Товар не найден");
    }

    @Test
    void addItem_alreadyInCart_throws() {
        CartItem existingItem = CartItem.builder()
            .id(UUID.randomUUID())
            .cart(cart)
            .product(product)
            .quantity(1)
            .build();
        cart.getItems().add(existingItem);

        ItemRequestDto request = new ItemRequestDto(productId, 2);

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> cartService.addItem(user, request))
            .isInstanceOf(ItemAlreadyInCartException.class)
            .hasMessage("Товар уже в корзине");
    }

    @Test
    void addItem_newCart_success() {
        ItemRequestDto request = new ItemRequestDto(productId, 2);

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(cartMapper.cartToCartDto(any(Cart.class))).thenReturn(cartDto);

        CartDto result = cartService.addItem(user, request);

        assertThat(result).isEqualTo(cartDto);
    }

    @Test
    void updateItemQuantity_success() {
        CartItem cartItem = CartItem.builder()
            .id(UUID.randomUUID())
            .cart(cart)
            .product(product)
            .quantity(1)
            .build();
        cart.getItems().add(cartItem);

        UpdateQuantityRequestDto request = new UpdateQuantityRequestDto(5);

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(cartMapper.cartToCartDto(any(Cart.class))).thenReturn(cartDto);

        CartDto result = cartService.updateItemQuantity(user, productId, request);

        assertThat(result).isEqualTo(cartDto);
        assertThat(cartItem.getQuantity()).isEqualTo(5);
    }

    @Test
    void updateItemQuantity_zeroRemovesItem() {
        CartItem cartItem = CartItem.builder()
            .id(UUID.randomUUID())
            .cart(cart)
            .product(product)
            .quantity(1)
            .build();
        cart.getItems().add(cartItem);

        UpdateQuantityRequestDto request = new UpdateQuantityRequestDto(0);

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(cartMapper.cartToCartDto(any(Cart.class))).thenReturn(cartDto);

        cartService.updateItemQuantity(user, productId, request);

        assertThat(cart.getItems()).isEmpty();
    }

    @Test
    void updateItemQuantity_cartNotFound_throws() {
        UpdateQuantityRequestDto request = new UpdateQuantityRequestDto(5);
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.updateItemQuantity(user, productId, request))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("Корзина не найдена");
    }

    @Test
    void updateItemQuantity_itemNotFound_throws() {
        UpdateQuantityRequestDto request = new UpdateQuantityRequestDto(5);
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        assertThatThrownBy(() -> cartService.updateItemQuantity(user, UUID.randomUUID(), request))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("Товар не найден в корзине");
    }

    @Test
    void removeItem_success() {
        CartItem cartItem = CartItem.builder()
            .id(UUID.randomUUID())
            .cart(cart)
            .product(product)
            .quantity(1)
            .build();
        cart.getItems().add(cartItem);

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(cartMapper.cartToCartDto(any(Cart.class))).thenReturn(cartDto);

        cartService.removeItem(user, productId);

        assertThat(cart.getItems()).isEmpty();
    }

    @Test
    void removeItem_cartNotFound_throws() {
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.removeItem(user, productId))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("Корзина не найдена");
    }

    @Test
    void removeItem_itemNotFound_throws() {
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        assertThatThrownBy(() -> cartService.removeItem(user, UUID.randomUUID()))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("Товар не найден в корзине");
    }

    @Test
    void clearCart_success() {
        CartItem cartItem = CartItem.builder()
            .id(UUID.randomUUID())
            .cart(cart)
            .product(product)
            .quantity(1)
            .build();
        cart.getItems().add(cartItem);

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(cartMapper.cartToCartDto(any(Cart.class))).thenReturn(cartDto);

        cartService.clearCart(user);

        assertThat(cart.getItems()).isEmpty();
        verify(cartRepository).save(cart);
    }

    @Test
    void clearCart_noExistingCart_createsNew() {
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(cartMapper.cartToCartDto(any(Cart.class))).thenReturn(cartDto);

        CartDto result = cartService.clearCart(user);

        assertThat(result).isEqualTo(cartDto);
    }
}
