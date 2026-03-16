package org.sunrider.market.product.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.sunrider.market.exception.NotFoundException;
import org.sunrider.market.product.dto.CategoryDto;
import org.sunrider.market.product.dto.ProductDto;
import org.sunrider.market.product.service.ProductService;
import org.sunrider.market.security.JwtService;
import org.sunrider.market.user.service.UserService;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserService userService;

    private ProductDto productDto;
    private UUID productId;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        CategoryDto categoryDto = new CategoryDto(UUID.randomUUID(), "Электроника");
        productDto = new ProductDto(productId, "Iphone 13", "Смартфон Apple",
            BigDecimal.valueOf(30000), 50, categoryDto, Collections.emptyList());
    }

    @Test
    void getProducts_success() throws Exception {
        when(productService.getProducts(0,10)).thenReturn(new PageImpl<>(Collections.singletonList(productDto)));

        mockMvc.perform(get("/api/v1/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].name").value("Iphone 13"))
            .andExpect(jsonPath("$.content[0].price").value(30000));
    }

    @Test
    void getProduct_success() throws Exception {
        when(productService.getProductById(productId)).thenReturn(productDto);

        mockMvc.perform(get("/api/v1/products/{id}", productId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Iphone 13"));
    }

    @Test
    void getProduct_notFound() throws Exception {
        UUID unknownId = UUID.randomUUID();
        when(productService.getProductById(unknownId))
            .thenThrow(new NotFoundException("Нет товара с ID: " + unknownId));

        mockMvc.perform(get("/api/v1/products/{id}", unknownId))
            .andExpect(status().isNotFound());
    }

    @Test
    void createProduct_success() throws Exception {
        when(productService.createProduct(any(ProductDto.class))).thenReturn(productDto);

        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Iphone 13"));
    }

    @Test
    void updateProduct_success() throws Exception {
        when(productService.updateProduct(any(ProductDto.class))).thenReturn(productDto);

        mockMvc.perform(put("/api/v1/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(productDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Iphone 13"));
    }

    @Test
    void deleteProduct_success() throws Exception {
        doNothing().when(productService).deleteProduct(productId);

        mockMvc.perform(delete("/api/v1/products/{id}", productId))
            .andExpect(status().isOk());
    }

    @Test
    void deleteProduct_notFound() throws Exception {
        UUID unknownId = UUID.randomUUID();
        doThrow(new NotFoundException("Нет товара с ID: " + unknownId))
            .when(productService).deleteProduct(unknownId);

        mockMvc.perform(delete("/api/v1/products/{id}", unknownId))
            .andExpect(status().isNotFound());
    }
}
