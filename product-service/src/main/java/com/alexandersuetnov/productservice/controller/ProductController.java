package com.alexandersuetnov.productservice.controller;

import com.alexandersuetnov.productservice.dto.ProductDTO;
import com.alexandersuetnov.productservice.mappers.ProductMapper;
import com.alexandersuetnov.productservice.model.Product;
import com.alexandersuetnov.productservice.service.ProductService;
import com.alexandersuetnov.productservice.validation.ResponseErrorValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("api/v1/products")
@RequiredArgsConstructor
@Log4j2
public class ProductController {

    private final ProductService productService;
    private final ResponseErrorValidation responseErrorValidation;


    @PostMapping("/create")

    public ResponseEntity<Object> createProduct(@Valid @RequestBody
                                                        ProductDTO productDTO,
                                                BindingResult bindingResult,
                                                Principal principal) {
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) return errors;
        Product product = productService.createProduct(productDTO);
        ProductDTO createdProduct = ProductMapper.INSTANCE.ProductToProductDTO(product);
        log.info("Save new product with name {} to database", productDTO.getTitle());
        return new ResponseEntity<>(createdProduct, HttpStatus.OK);
    }


    @GetMapping("/info/{productId}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable("productId") String productId) {
        Product product = productService.getProductById(Long.parseLong(productId));
        ProductDTO productDTO = ProductMapper.INSTANCE.ProductToProductDTO(product);
        product.add(linkTo(methodOn(ProductController.class)
                        .getProduct(productId))
                        .withSelfRel(),
                linkTo(methodOn(ProductController.class)
                        .updateProduct(productId, productDTO))
                        .withRel("updateProduct"),
                linkTo(methodOn(ProductController.class)
                        .deleteProduct(productId))
                        .withRel("deleteProduct"));
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
    public ResponseEntity<String> deleteProduct(@PathVariable("productId") String productId) {
        return ResponseEntity.ok(productService.deleteProduct(productId));
    }

    @PutMapping("/update/{productId}")
    public ResponseEntity<String> updateProduct(
            @PathVariable("productId") String productId,
            @RequestBody ProductDTO productDTO) {
        return ResponseEntity.ok(productService.updateProduct(productDTO, productId));
    }

}
