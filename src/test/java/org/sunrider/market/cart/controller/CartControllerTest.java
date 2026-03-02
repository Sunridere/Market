package org.sunrider.market.cart.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.sunrider.market.cart.dto.CartDto;
import org.sunrider.market.cart.dto.CartItemDto;
import org.sunrider.market.cart.dto.ItemRequestDto;
import org.sunrider.market.cart.dto.UpdateQuantityRequestDto;
import org.sunrider.market.cart.service.CartService;
import org.sunrider.market.exception.NotFoundException;
import org.sunrider.market.security.JwtService;
import org.sunrider.market.user.entity.Role;
import org.sunrider.market.user.entity.User;
import org.sunrider.market.user.service.UserService;

@WebMvcTest(CartController.class)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CartService cartService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserService userService;

    private User testUser;
    private CartDto cartDto;
    private UUID productId;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        testUser = User.builder()
            .id(UUID.randomUUID())
            .username("testuser")
            .email("test@test.com")
            .password("encoded")
            .role(Role.ROLE_USER)
            .build();

        CartItemDto cartItemDto = new CartItemDto(
            UUID.randomUUID(), productId, "Iphone 13", 2,
            BigDecimal.valueOf(30000), BigDecimal.valueOf(60000));
        cartDto = new CartDto(UUID.randomUUID(), List.of(cartItemDto), BigDecimal.valueOf(60000));
    }

    @Test
    void getCart_success() throws Exception {
        when(cartService.getCart(any(User.class))).thenReturn(cartDto);

        mockMvc.perform(get("/api/v1/cart")
                .with(user(testUser)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalCartPrice").value(60000))
            .andExpect(jsonPath("$.items[0].name").value("Iphone 13"));
    }

    @Test
    void addItem_success() throws Exception {
        ItemRequestDto request = new ItemRequestDto(productId, 2);
        when(cartService.addItem(any(User.class), any(ItemRequestDto.class))).thenReturn(cartDto);

        mockMvc.perform(post("/api/v1/cart/items")
                .with(user(testUser))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items[0].productId").value(productId.toString()));
    }

    @Test
    void updateItemQuantity_success() throws Exception {
        UpdateQuantityRequestDto request = new UpdateQuantityRequestDto(5);
        when(cartService.updateItemQuantity(any(User.class), eq(productId), any(UpdateQuantityRequestDto.class)))
            .thenReturn(cartDto);

        mockMvc.perform(put("/api/v1/cart/items/{productId}", productId)
                .with(user(testUser))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());
    }

    @Test
    void removeItem_success() throws Exception {
        when(cartService.removeItem(any(User.class), eq(productId))).thenReturn(cartDto);

        mockMvc.perform(delete("/api/v1/cart/items/{productId}", productId)
                .with(user(testUser))
                .with(csrf()))
            .andExpect(status().isOk());
    }

    @Test
    void clearCart_success() throws Exception {
        CartDto emptyCart = new CartDto(UUID.randomUUID(), List.of(), BigDecimal.ZERO);
        when(cartService.clearCart(any(User.class))).thenReturn(emptyCart);

        mockMvc.perform(delete("/api/v1/cart/clear")
                .with(user(testUser))
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items").isEmpty())
            .andExpect(jsonPath("$.totalCartPrice").value(0));
    }

    @Test
    void removeItem_notFound() throws Exception {
        UUID unknownProductId = UUID.randomUUID();
        when(cartService.removeItem(any(User.class), eq(unknownProductId)))
            .thenThrow(new NotFoundException("Товар не найден в корзине"));

        mockMvc.perform(delete("/api/v1/cart/items/{productId}", unknownProductId)
                .with(user(testUser))
                .with(csrf()))
            .andExpect(status().isNotFound());
    }
}
