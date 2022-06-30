package com.alexandersuetnov.imageservice.controller;

import com.alexandersuetnov.imageservice.model.Image;
import com.alexandersuetnov.imageservice.repository.ImageRepository;
import com.alexandersuetnov.imageservice.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@EnableFeignClients
@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
@Log4j2
public class ImageController {

    private final ImageService imageService;
    private final ImageRepository imageRepository;


    @PostMapping("/{productId}/upload")
    public ResponseEntity<MessageResponse> uploadImageToProduct(
            @PathVariable("productId") String productId, HttpServletRequest request,
            @RequestParam("file") MultipartFile file) throws IOException {
        imageService.uploadImageToProduct(file, request, productId);
        return ResponseEntity.ok(new MessageResponse("Image upload successfully"));
    }

    @DeleteMapping("/delete/{imageId}")
    public ResponseEntity<MessageResponse> deleteProduct(
            @PathVariable("imageId") String imageId, HttpServletRequest request) {
        log.info("Delete product with id {}", imageId);
        return (imageService.deleteImage(imageId, request)) ?
                new ResponseEntity<>(new MessageResponse("Product was deleted"), HttpStatus.OK) :
                new ResponseEntity<>(new MessageResponse("Product was not deleted"), HttpStatus.FORBIDDEN);
    }

    //просмотр
    @GetMapping("/{productId}/image")
    private ResponseEntity<?> getImageById(@PathVariable Long productId) {
        Image image = imageRepository.findByProductId(productId);
        return ResponseEntity.ok()
                .header("fileName", image.getOriginalFileName())
                .contentType(MediaType.valueOf(image.getContentType()))
                .contentLength(image.getSize())
                .body(new InputStreamResource(new ByteArrayInputStream(image.getBytes())));
    }

}
