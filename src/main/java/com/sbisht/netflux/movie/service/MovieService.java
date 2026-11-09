package com.sbisht.netflux.movie.service;

import com.sbisht.netflux.movie.dto.MovieDetails;
import com.sbisht.netflux.movie.dto.MovieSearchRequest;
import com.sbisht.netflux.movie.dto.MovieSummary;
import com.sbisht.netflux.movie.entity.Movie;
import com.sbisht.netflux.movie.exceptions.MovieNotFoundException;
import com.sbisht.netflux.movie.mapper.MovieMapper;
import com.sbisht.netflux.movie.repository.MovieRepository;
import com.sbisht.netflux.movie.repository.MovieSpecifications;
import com.sbisht.netflux.movie.util.LimitResolver;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class MovieService {

    private static final Sort BY_VOTE_COUNT = Sort.by(
            Sort.Order.desc("voteCount").nullsLast(),
            Sort.Order.asc("id")
    );
    private static final Sort BY_RELEASE_DATE = Sort.by(
            Sort.Order.desc("releaseDate"),
            Sort.Order.asc("id")
    );

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public MovieDetails findById(Long id) {
        return movieRepository.findById(id)
                .map(MovieMapper::toDetails)
                .orElseThrow(() -> new MovieNotFoundException(id));
    }

    public List<MovieSummary> findByIds(List<Long> ids) {
        var uniqueIds = ids.stream()
                .distinct()
                .toList();
        var moviesById = movieRepository.findAllById(uniqueIds)
                .stream()
                .collect(Collectors.toMap(Movie::getId, Function.identity()));
        return uniqueIds.stream()
                .map(moviesById::get)
                .filter(Objects::nonNull)
                .map(MovieMapper::toSummary)
                .toList();
    }

    public List<MovieSummary> findPopular(Integer limit) {
        return toSummaries(movieRepository.findAllBy(BY_VOTE_COUNT, Limit.of(LimitResolver.resolve(limit))));
    }

    public List<MovieSummary> findLatest(Integer limit) {
        return toSummaries(movieRepository.findByReleaseDateLessThanEqual(
                LocalDate.now(),
                BY_RELEASE_DATE,
                Limit.of(LimitResolver.resolve(limit))
        ));
    }

    public List<MovieSummary> search(MovieSearchRequest request) {
        var spec = MovieSpecifications.titleContains(request.title())
                .and(MovieSpecifications.hasGenre(request.genre()));
        var movies = movieRepository.findBy(spec, query -> query
                .sortBy(BY_VOTE_COUNT)
                .limit(LimitResolver.resolve(request.limit()))
                .all());
        return toSummaries(movies);
    }

    private List<MovieSummary> toSummaries(List<Movie> movies) {
        return movies.stream()
                .map(MovieMapper::toSummary)
                .toList();
    }
}
