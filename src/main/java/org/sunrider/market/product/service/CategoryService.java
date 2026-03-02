package org.sunrider.market.product.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.sunrider.market.exception.NotFoundException;
import org.sunrider.market.product.dto.CategoryDto;
import org.sunrider.market.product.entity.Category;
import org.sunrider.market.product.mapper.ProductMapper;
import org.sunrider.market.product.repository.CategoryRepository;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    public List<CategoryDto> getAllCategories() {
         return productMapper.categoryToCategoryDtos(categoryRepository.findAll());
    }

    public CategoryDto createCategory(CategoryDto categoryDto) {
        return productMapper.categoryToCategoryDto(categoryRepository.save(Category.builder()
            .name(categoryDto.name())
            .build()));
    }

    public Category findCategoryByName(String name) {
        return categoryRepository.findByName(name)
            .orElseThrow(() -> new NotFoundException("Такой категории не существует"));
    }

}
