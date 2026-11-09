package com.sbisht.netflux.movie.repository;

import com.sbisht.netflux.movie.entity.Movie;
import java.util.Objects;
import org.springframework.data.jpa.domain.Specification;

public class MovieSpecifications {

    private static final char ESCAPE_CHAR = '\\';

    public static Specification<Movie> titleContains(String title) {
        return (root, query, cb) ->
                isBlank(title) ? cb.conjunction() :
                cb.like(cb.lower(root.get("title")), "%" + escapeLike(title.strip().toLowerCase()) + "%", ESCAPE_CHAR);
    }

    public static Specification<Movie> hasGenre(String genre) {
        return (root, query, cb) ->
                isBlank(genre) ? cb.conjunction() :
                cb.greaterThan(cb.function("array_position", Integer.class, root.get("genres"), cb.literal(genre.strip())), 0);
    }

    private static boolean isBlank(String value) {
        return Objects.isNull(value) || value.isBlank();
    }

    private static String escapeLike(String value) {
        return value.replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
}
