package com.alexandersuetnov.productservice.controller;

import com.alexandersuetnov.productservice.dto.ProductDTO;
import com.alexandersuetnov.productservice.mappers.ProductMapper;
import com.alexandersuetnov.productservice.model.Product;
import com.alexandersuetnov.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/products")
@RequiredArgsConstructor
@Log4j2
public class ProductController {

    private final ProductService productService;

    @GetMapping("/info/{productId}")
    public ResponseEntity<Product> getProduct(@PathVariable("productId") String productId) {
        Product product = productService.getProductById(Long.parseLong(productId));
        return ResponseEntity.ok(product);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> productDTOList = productService.getAllProduct()
                .stream()
                .map(ProductMapper.INSTANCE::ProductToProductDTO)
                .collect(Collectors.toList());
        log.info("Get get all products");
        return new ResponseEntity<>(productDTOList, HttpStatus.OK);
    }


}
