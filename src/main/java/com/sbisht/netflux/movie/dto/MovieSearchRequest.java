package com.sbisht.netflux.movie.dto;

import jakarta.validation.constraints.AssertTrue;
import java.util.Objects;

public record MovieSearchRequest(String title,
                                 String genre,
                                 Integer limit) {

    @AssertTrue(message = "At least one of title or genre is required")
    public boolean isAtLeastOnePresent() {
        return hasText(title) || hasText(genre);
    }

    private static boolean hasText(String value) {
        return Objects.nonNull(value) && !value.isBlank();
    }
}
