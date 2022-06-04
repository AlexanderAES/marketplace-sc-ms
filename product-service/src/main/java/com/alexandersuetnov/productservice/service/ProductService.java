package com.alexandersuetnov.productservice.service;

import com.alexandersuetnov.productservice.dto.ProductDTO;
import com.alexandersuetnov.productservice.exception.ProductNotFoundException;
import com.alexandersuetnov.productservice.mappers.ProductMapper;
import com.alexandersuetnov.productservice.model.Product;
import com.alexandersuetnov.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product createProduct(ProductDTO productDTO) {
            Product product = ProductMapper.INSTANCE.ProductDTOtoProduct(productDTO);
            log.info("Saving product for user with id: {}", product.getUserId());
            return productRepository.save(product);
    }

    public Product getProductById(Long productId) {
        return productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId));
    }

    // возвращает все объявления из бд удовлетворяющие поисковому слову
    public List<Product> getAllProducts(String title) {
        if (title != null)
            return productRepository.findByTitleContainingIgnoreCase(title);
        return productRepository.findAll();
    }

    public List<Product> getAllProduct() {
        return productRepository.findAll();
    }

}
