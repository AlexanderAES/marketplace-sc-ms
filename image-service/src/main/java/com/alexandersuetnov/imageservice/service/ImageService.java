package com.alexandersuetnov.imageservice.service;

import com.alexandersuetnov.imageservice.config.JWTFilter;
import com.alexandersuetnov.imageservice.model.Image;
import com.alexandersuetnov.imageservice.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@Log4j2
public class ImageService {

    private final ImageRepository imageRepository;
    private final JWTFilter jwtFilter;

    public Image uploadImageToProduct(MultipartFile file,
                                      HttpServletRequest request,
                                      String productId) throws IOException {
        String userId = jwtFilter.getUserIdFromRequest(request);
            Image image = new Image();
            image.setUserId(userId);
            image.setProductId(productId);
            image.setName(file.getName());
            image.setOriginalFileName(file.getOriginalFilename());
            image.setContentType(file.getContentType());
            image.setSize(file.getSize());
            image.setBytes(file.getBytes());
            log.info("Uploading image to product {}", image.getProductId());
            return imageRepository.save(image);

    }

    public boolean deleteImage(String imageId, HttpServletRequest requestCurrentUser) {
        Image image = getImageById(Long.parseLong(imageId));
        String currentOwnerImage = image.getUserId();
        if (isEquals(currentOwnerImage, requestCurrentUser)) {
            imageRepository.deleteById(Long.parseLong(imageId));
            log.info("Deleting product with id: {}", imageId);
            return true;
        }
        log.error("wrong delete product: {}", imageId);
        return false;
    }

    public boolean isEquals(String currentOwnerImage, HttpServletRequest request) {
        long currentUserId = Long.parseLong(jwtFilter.getUserIdFromRequest(request));
        return currentUserId == Long.parseLong(currentOwnerImage);
    }

    public Image getImageById(Long productId) {
        return imageRepository.findImageById(productId);
    }

}
