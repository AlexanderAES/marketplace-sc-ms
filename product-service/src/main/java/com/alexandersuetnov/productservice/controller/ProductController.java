package com.alexandersuetnov.productservice.controller;

import com.alexandersuetnov.productservice.config.JWTFilter;
import com.alexandersuetnov.productservice.dto.ProductDTO;
import com.alexandersuetnov.productservice.mappers.ProductMapper;
import com.alexandersuetnov.productservice.model.Product;
import com.alexandersuetnov.productservice.payload.MessageResponse;
import com.alexandersuetnov.productservice.service.ProductService;
import com.alexandersuetnov.productservice.validation.ResponseErrorValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/products")
@RequiredArgsConstructor
@Log4j2
public class ProductController {

    private final ProductService productService;
    private final ResponseErrorValidation responseErrorValidation;
    private final JWTFilter jwtFilter;


    @PostMapping("/create")
    public ResponseEntity<Object> createProduct(
            @Valid @RequestBody ProductDTO productDTO,
            BindingResult bindingResult,
            HttpServletRequest httpServletRequest) {
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) return errors;
        productDTO.setUserId(jwtFilter.getUserIdFromRequest(httpServletRequest));
        Product product = productService.createProduct(productDTO);
        ProductDTO createdProduct = ProductMapper.INSTANCE.ProductToProductDTO(product);
        log.info("Save new product with name {} to database", productDTO.getTitle());
        return new ResponseEntity<>(createdProduct, HttpStatus.OK);
    }


    @GetMapping("/info/{productId}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable("productId") String productId) {
        Product product = productService.getProductById(Long.parseLong(productId));
        ProductDTO productDTO = ProductMapper.INSTANCE.ProductToProductDTO(product);
        return new ResponseEntity<>(productDTO, HttpStatus.OK);
    }

    @GetMapping("/search/{title}")
    public ResponseEntity<List<ProductDTO>> getAllProductsByTitle(@PathVariable("title") String title) {
        List<ProductDTO> productDTOList = productService.getAllProductsByTitle(title)
                .stream()
                .map(ProductMapper.INSTANCE::ProductToProductDTO)
                .collect(Collectors.toList());
        log.info("Get get all products");
        return new ResponseEntity<>(productDTOList, HttpStatus.OK);
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

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<MessageResponse> deleteProduct(
            @PathVariable("productId") String productId, HttpServletRequest request) {
        log.info("Delete product with id {}", productId);
        return (productService.deleteProduct(productId, request)) ?
                new ResponseEntity<>(new MessageResponse("Product was deleted"), HttpStatus.OK) :
                new ResponseEntity<>(new MessageResponse("Product was not deleted"), HttpStatus.FORBIDDEN);
    }

    @PutMapping("/update/{productId}")
    public ResponseEntity<Object> updateProduct(
            @PathVariable("productId") String productId,
            @RequestBody ProductDTO productDTO,
            BindingResult bindingResult,
            HttpServletRequest request) {
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);

        if (!ObjectUtils.isEmpty(errors)) return errors;
        Product product = productService.updateProduct(productDTO, productId, request);
        ProductDTO productUpdated = ProductMapper.INSTANCE.ProductToProductDTO(product);

        if (productUpdated != null) return new ResponseEntity<>(productUpdated, HttpStatus.OK);
        log.info("Update product with name {} to database", productDTO.getTitle());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("The product has not been updated");

    }

    @GetMapping("/user/products")
    public ResponseEntity<List<ProductDTO>> getAllProductsForUser(HttpServletRequest request) {
        List<ProductDTO> productDTOList = productService.getAllProductFromUser(request)
                .stream()
                .map(ProductMapper.INSTANCE::ProductToProductDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(productDTOList, HttpStatus.OK);
    }


}
