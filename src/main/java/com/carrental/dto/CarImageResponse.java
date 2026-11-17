
package com.carrental.dto;

import java.time.LocalDateTime;

public record CarImageResponse(
        Long id,
        Long carId,
        String imageUrl,
        String publicId,
        Boolean isPrimary,
        LocalDateTime createdAt
) {
}
