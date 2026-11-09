package com.sbisht.netflux.movie.mapper;

import com.sbisht.netflux.movie.dto.MovieDetails;
import com.sbisht.netflux.movie.dto.MovieSummary;
import com.sbisht.netflux.movie.entity.Movie;
import java.util.List;
import java.util.Objects;

public class MovieMapper {

    public static MovieSummary toSummary(Movie movie) {
        return new MovieSummary(
                movie.getId(),
                movie.getTitle(),
                movie.getPosterPath(),
                movie.getBackdropPath(),
                movie.getVoteAverage(),
                movie.getReleaseDate(),
                nullToEmpty(movie.getGenres())
        );
    }

    public static MovieDetails toDetails(Movie movie) {
        return new MovieDetails(
                movie.getId(),
                movie.getTitle(),
                movie.getVoteAverage(),
                movie.getVoteCount(),
                movie.getReleaseDate(),
                movie.getRevenue(),
                movie.getRuntime(),
                movie.getBackdropPath(),
                movie.getBudget(),
                movie.getHomepage(),
                movie.getOverview(),
                movie.getPopularity(),
                movie.getPosterPath(),
                nullToEmpty(movie.getGenres()),
                nullToEmpty(movie.getThemes())
        );
    }

    private static List<String> nullToEmpty(List<String> values) {
        return Objects.isNull(values) ? List.of() : List.copyOf(values);
    }
}
