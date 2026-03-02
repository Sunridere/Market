package org.sunrider.market.product.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.sunrider.market.exception.NotFoundException;
import org.sunrider.market.product.dto.ProductDto;
import org.sunrider.market.product.mapper.ProductMapper;
import org.sunrider.market.product.repository.ProductRepository;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public List<ProductDto> getProducts() {
        return productMapper.productsToProductDtos(productRepository.findAllBy());
    }

    public List<ProductDto> getProducts(Set<UUID> productIds) {
        return productMapper.productsToProductDtos(productRepository.findAllById(productIds));
    }

    public ProductDto getProductById(UUID id) {
        return productMapper.productToProductDto(productRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Нет товара с ID: " + id)));
    }

}
