package org.sunrider.market.product.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.sunrider.market.product.dto.CategoryDto;
import org.sunrider.market.product.dto.ProductDto;
import org.sunrider.market.product.entity.Category;
import org.sunrider.market.product.entity.Product;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    ProductDto productToProductDto(Product product);

    List<ProductDto> productsToProductDtos(List<Product> products);

    CategoryDto categoryToCategoryDto(Category category);

    List<CategoryDto> categoryToCategoryDtos(List<Category> categories);

}
