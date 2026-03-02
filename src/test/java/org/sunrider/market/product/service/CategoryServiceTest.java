package org.sunrider.market.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sunrider.market.exception.NotFoundException;
import org.sunrider.market.product.dto.CategoryDto;
import org.sunrider.market.product.entity.Category;
import org.sunrider.market.product.mapper.ProductMapper;
import org.sunrider.market.product.repository.CategoryRepository;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryDto categoryDto;
    private UUID categoryId;

    @BeforeEach
    void setUp() {
        categoryId = UUID.randomUUID();
        category = Category.builder()
            .id(categoryId)
            .name("Электроника")
            .build();
        categoryDto = new CategoryDto(categoryId, "Электроника");
    }

    @Test
    void getAllCategories_success() {
        List<Category> categories = List.of(category);
        List<CategoryDto> dtos = List.of(categoryDto);

        when(categoryRepository.findAll()).thenReturn(categories);
        when(productMapper.categoryToCategoryDtos(categories)).thenReturn(dtos);

        List<CategoryDto> result = categoryService.getAllCategories();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Электроника");
    }

    @Test
    void createCategory_success() {
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(productMapper.categoryToCategoryDto(category)).thenReturn(categoryDto);

        CategoryDto result = categoryService.createCategory(categoryDto);

        assertThat(result.name()).isEqualTo("Электроника");
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void findCategoryByName_found() {
        when(categoryRepository.findByName("Электроника")).thenReturn(Optional.of(category));

        Category result = categoryService.findCategoryByName("Электроника");

        assertThat(result.getName()).isEqualTo("Электроника");
    }

    @Test
    void findCategoryByName_notFound_throws() {
        when(categoryRepository.findByName("Unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.findCategoryByName("Unknown"))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("Такой категории не существует");
    }
}
