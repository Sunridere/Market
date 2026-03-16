package org.sunrider.market.product.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sunrider.market.exception.NotFoundException;
import org.sunrider.market.product.dto.ProductDto;
import org.sunrider.market.product.dto.ProductImageDto;
import org.sunrider.market.product.entity.Category;
import org.sunrider.market.product.entity.Product;
import org.sunrider.market.product.entity.ProductImage;
import org.sunrider.market.product.mapper.ProductMapper;
import org.sunrider.market.product.repository.ProductRepository;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final CategoryService categoryService;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public Page<ProductDto> getProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAllByDeletedFalse(pageable).map(productMapper::productToProductDto);
    }

    @Transactional(readOnly = true)
    public List<ProductDto> getProducts(Set<UUID> productIds) {
        return productMapper.productsToProductDtos(productRepository.findAllByIdInAndDeletedFalse(productIds));
    }

    @Transactional(readOnly = true)
    public ProductDto getProductById(UUID id) {
        return productMapper.productToProductDto(productRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new NotFoundException("Нет товара с ID: " + id)));
    }

    @Transactional
    public ProductDto createProduct(ProductDto productDto) {

        Category category = categoryService.findCategoryByName(productDto.category().name());

        Product product = Product.builder()
            .name(productDto.name())
            .description(productDto.description())
            .price(productDto.price())
            .stockQuantity(productDto.stockQuantity())
            .category(category)
            .build();

        if (productDto.images() != null) {
            for (ProductImageDto imageDto : productDto.images()) {
                ProductImage image = ProductImage.builder()
                    .product(product)
                    .imageUrl(imageDto.imageUrl())
                    .isMain(imageDto.isMain() != null ? imageDto.isMain() : false)
                    .build();
                product.getImages().add(image);
            }
        }

        return productMapper.productToProductDto(productRepository.save(product));
    }

    @Transactional
    public ProductDto updateProduct(ProductDto productDto) {
        Product product = productRepository.findByIdAndDeletedFalse(productDto.id())
            .orElseThrow(() -> new NotFoundException("Не найден продукт с ID: " + productDto.id()));

        product.setName(productDto.name());
        product.setDescription(productDto.description());
        product.setPrice(productDto.price());
        product.setStockQuantity(productDto.stockQuantity());
        product.setCategory(categoryService.findCategoryByName(productDto.category().name()));

        product.getImages().clear();
        if (productDto.images() != null) {
            for (ProductImageDto imageDto : productDto.images()) {
                ProductImage image = ProductImage.builder()
                    .product(product)
                    .imageUrl(imageDto.imageUrl())
                    .isMain(imageDto.isMain() != null ? imageDto.isMain() : false)
                    .build();
                product.getImages().add(image);
            }
        }

        return productMapper.productToProductDto(productRepository.save(product));
    }

    @Transactional
    public void deleteProduct(UUID id) {
        Product product = productRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new NotFoundException("Нет товара с ID: " + id));
        product.setDeleted(true);
        productRepository.save(product);
    }

}
