package org.sunrider.market.product.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.sunrider.market.product.dto.ProductDto;
import org.sunrider.market.product.entity.Product;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    @Mapping(source = "category.name", target = "category")
    ProductDto productToProductDto(Product product);

    List<ProductDto> productsToProductDtos(List<Product> products);

}
