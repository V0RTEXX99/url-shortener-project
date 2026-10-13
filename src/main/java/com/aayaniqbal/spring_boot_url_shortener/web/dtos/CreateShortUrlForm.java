package com.aayaniqbal.spring_boot_url_shortener.web.dtos;

import jakarta.validation.constraints.NotBlank;

public record CreateShortUrlForm(

        @NotBlank(message = "Original URL is required")
        String originalUrl

) {

}
