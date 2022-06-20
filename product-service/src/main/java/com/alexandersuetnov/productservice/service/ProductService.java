package com.alexandersuetnov.productservice.service;

import com.alexandersuetnov.productservice.config.JWTFilter;
import com.alexandersuetnov.productservice.dto.ProductDTO;
import com.alexandersuetnov.productservice.exception.ProductNotFoundException;
import com.alexandersuetnov.productservice.mappers.ProductMapper;
import com.alexandersuetnov.productservice.model.Product;
import com.alexandersuetnov.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final JWTFilter jwtFilter;

    public Product createProduct(ProductDTO productDTO) {
        Product product = ProductMapper.INSTANCE.ProductDTOtoProduct(productDTO);
        log.info("Saving product for user with id: {}", product.getUserId());
        return productRepository.save(product);
    }

    public Product getProductById(Long productId) {
        return productRepository.findProductById(productId);
    }


    public List<Product> getAllProductsByTitle(String title) {
        if (title != null)
            return productRepository.findByTitleContainingIgnoreCase(title);
        return productRepository.findAll();
    }

    public List<Product> getAllProduct() {
        return productRepository.findAll();
    }

    public boolean deleteProduct(String productId,HttpServletRequest requestCurrentUser) {
        Product currentProduct = getProductById(Long.parseLong(productId));
        if (isEquals(currentProduct,requestCurrentUser)){
            productRepository.deleteById(Long.parseLong(productId));
            log.info("Deleting product with id: {}", productId);
            return true;
        }
        log.error("wrong delete product: {}", productId);
        return false;
    }

    public Product updateProduct(ProductDTO productDTO, String productId, HttpServletRequest requestCurrentUser) {
        Product currentProduct = getProductById(Long.parseLong(productId));
        if (isEquals(currentProduct,requestCurrentUser)){
            productDTO.setUserId(jwtFilter.getUserIdFromRequest(requestCurrentUser));
            currentProduct = ProductMapper.INSTANCE.ProductDTOtoProduct(productDTO);
            log.info("Update product with title {}", productDTO.getTitle());
            return productRepository.save(currentProduct);
        }
        return null;
    }

    public boolean isEquals(Product currentProduct, HttpServletRequest request) {
        long currentUserId = Long.parseLong(jwtFilter.getUserIdFromRequest(request));
        long productOwnerId = Long.parseLong(currentProduct.getUserId());
        return currentUserId == productOwnerId;
    }

    public List<Product> getAllProductFromUser(HttpServletRequest request) {
        long userId = Long.parseLong(jwtFilter.getUserIdFromRequest(request));
        return productRepository.findAllByUserIdOrderByCreateDateDesc(userId);
    }

}
