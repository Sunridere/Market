package org.sunrider.market.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sunrider.market.exception.NotFoundException;
import org.sunrider.market.product.dto.CategoryDto;
import org.sunrider.market.product.dto.ProductDto;
import org.sunrider.market.product.entity.Category;
import org.sunrider.market.product.entity.Product;
import org.sunrider.market.product.mapper.ProductMapper;
import org.sunrider.market.product.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private CategoryService categoryService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductDto productDto;
    private Category category;
    private CategoryDto categoryDto;
    private UUID productId;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        category = Category.builder()
            .id(categoryId)
            .name("Электроника")
            .build();

        categoryDto = new CategoryDto(categoryId, "Электроника");

        product = Product.builder()
            .id(productId)
            .name("Iphone 13")
            .description("Смартфон Apple")
            .price(BigDecimal.valueOf(30000))
            .stockQuantity(50)
            .category(category)
            .build();

        productDto = new ProductDto(productId, "Iphone 13", "Смартфон Apple",
            BigDecimal.valueOf(30000), 50, categoryDto);
    }

    @Test
    void getProducts_success() {
        List<Product> products = List.of(product);
        List<ProductDto> dtos = List.of(productDto);

        when(productRepository.findAllBy()).thenReturn(products);
        when(productMapper.productsToProductDtos(products)).thenReturn(dtos);

        List<ProductDto> result = productService.getProducts();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Iphone 13");
    }

    @Test
    void getProducts_byIds_success() {
        Set<UUID> ids = Set.of(productId);
        List<Product> products = List.of(product);
        List<ProductDto> dtos = List.of(productDto);

        when(productRepository.findAllById(ids)).thenReturn(products);
        when(productMapper.productsToProductDtos(products)).thenReturn(dtos);

        List<ProductDto> result = productService.getProducts(ids);

        assertThat(result).hasSize(1);
    }

    @Test
    void getProductById_success() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productMapper.productToProductDto(product)).thenReturn(productDto);

        ProductDto result = productService.getProductById(productId);

        assertThat(result.name()).isEqualTo("Iphone 13");
    }

    @Test
    void getProductById_notFound_throws() {
        UUID unknownId = UUID.randomUUID();
        when(productRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(unknownId))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("Нет товара с ID");
    }

    @Test
    void createProduct_success() {
        when(categoryService.findCategoryByName("Электроника")).thenReturn(category);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.productToProductDto(product)).thenReturn(productDto);

        ProductDto result = productService.createProduct(productDto);

        assertThat(result.name()).isEqualTo("Iphone 13");
        verify(productRepository).save(any(Product.class));
    }
}
