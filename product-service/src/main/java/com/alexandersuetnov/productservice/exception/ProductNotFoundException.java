package com.alexandersuetnov.productservice.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long id) {
        super(String.format("Products with id '%s' not found", id));
    }
}
