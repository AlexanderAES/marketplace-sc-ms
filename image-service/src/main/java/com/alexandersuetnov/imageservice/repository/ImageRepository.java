package com.alexandersuetnov.imageservice.repository;

import com.alexandersuetnov.imageservice.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image,Long> {
    Image findByProductId(Long productId);

    Image findImageById(Long productId);
}
