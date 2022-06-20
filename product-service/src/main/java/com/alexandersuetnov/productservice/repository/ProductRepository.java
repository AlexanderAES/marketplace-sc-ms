package com.alexandersuetnov.productservice.repository;

import com.alexandersuetnov.productservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

    Product findProductById(Long productId);

    List<Product> findByTitleContainingIgnoreCase(String title);

    List<Product> findAllByUserIdOrderByCreateDateDesc(Long userId);
}
