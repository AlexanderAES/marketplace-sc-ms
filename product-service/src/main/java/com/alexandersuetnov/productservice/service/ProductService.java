package com.alexandersuetnov.productservice.service;

import com.alexandersuetnov.productservice.dto.ProductDTO;
import com.alexandersuetnov.productservice.exception.ProductNotFoundException;
import com.alexandersuetnov.productservice.mappers.ProductMapper;
import com.alexandersuetnov.productservice.model.Product;
import com.alexandersuetnov.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product createProduct(ProductDTO productDTO, Principal principal) {
            Product product = ProductMapper.INSTANCE.ProductDTOtoProduct(productDTO);
            log.info("Saving product for user with id: {}", product.getUserId());
            return productRepository.save(product);
    }

    public Product getProductById(Long productId) {
        return productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId));
    }


    public List<Product> getAllProductsByTitle(String title) {
        if (title != null)
            return productRepository.findByTitleContainingIgnoreCase(title);
        return productRepository.findAll();
    }

    public List<Product> getAllProduct() {
        return productRepository.findAll();
    }

    public String deleteProduct(String productId) {
        String responseMessage = String.format("Deleting product with id %s ", productId);
        productRepository.deleteById(Long.parseLong(productId));
        log.info("Deleting product with id: {}", productId);
        return responseMessage;
    }

    public String updateProduct(ProductDTO productDTO,String userId,String productId) {
        Product currentProduct = getProductById(Long.parseLong(productId));
        String responseMessage;
        if (productDTO != null){

            //ToDo сделать проверку на соответствие продукта и пользователя isEquals(){}

            currentProduct = ProductMapper.INSTANCE.ProductDTOtoProduct(productDTO);
            currentProduct.setUserId(userId);
            responseMessage = String.format("Update product with title {}", productDTO.getTitle());
            log.info("Update product with title {}", productDTO.getTitle());
            return responseMessage;
        }
        return null;
    }

//    Todo добавить метод "boolean isEquals(){}"


}
