
package com.carrental.service;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    @Value("${app.car-image.max-size-bytes:5242880}")
    private long maxFileSize;

    private static final Set<String> ALLOWED_CONTENT_TYPES =
            Set.of("image/jpeg", "image/png", "image/webp");

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public Map<String, Object> uploadImage(MultipartFile file) {

        validateImage(file);

        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "car-rental/cars",
                            "public_id", UUID.randomUUID().toString(),
                            "resource_type", "image"
                    )
            );

            return Map.of(
                    "imageUrl", result.get("secure_url").toString(),
                    "publicId", result.get("public_id").toString()
            );

        } catch (IOException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Unable to upload image to Cloudinary",
                    exception
            );
        }
    }

    public void deleteImage(String publicId) {

        if (publicId == null || publicId.isBlank()) {
            return;
        }

        try {
            Map<?, ?> result = cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.asMap("resource_type", "image")
            );

            Object status = result.get("result");

            if (!"ok".equals(status) && !"not found".equals(status)) {
                throw new IllegalStateException(
                        "Cloudinary image deletion failed: " + status
                );
            }

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Unable to delete image from Cloudinary",
                    exception
            );
        }
    }

    private void validateImage(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Please select an image"
            );
        }

        if (file.getSize() > maxFileSize) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Image size must not exceed 5 MB"
            );
        }

        String contentType = file.getContentType();

        if (contentType == null ||
                !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only JPEG, PNG and WebP images are allowed"
            );
        }
    }
}
