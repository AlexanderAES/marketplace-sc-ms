package com.alexandersuetnov.productservice.mappers;

import com.alexandersuetnov.productservice.dto.ProductDTO;
import com.alexandersuetnov.productservice.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    ProductDTO ProductToProductDTO (Product product);

    Product ProductDTOtoProduct (ProductDTO productDTO);

}
