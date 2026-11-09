package com.sbisht.netflux.movie.controller;

import com.sbisht.netflux.movie.dto.MovieDetails;
import com.sbisht.netflux.movie.dto.MovieSearchRequest;
import com.sbisht.netflux.movie.dto.MovieSummary;
import com.sbisht.netflux.movie.service.MovieService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/{id}")
    public MovieDetails getById(@PathVariable Long id) {
        return movieService.findById(id);
    }

    @GetMapping("/batch")
    public List<MovieSummary> getByIds(@RequestParam @NotEmpty @Size(max = 100) List<@NotNull Long> ids) {
        return movieService.findByIds(ids);
    }

    @GetMapping("/popular")
    public List<MovieSummary> getPopular(@RequestParam(required = false) Integer limit) {
        return movieService.findPopular(limit);
    }

    @GetMapping("/latest")
    public List<MovieSummary> getLatest(@RequestParam(required = false) Integer limit) {
        return movieService.findLatest(limit);
    }

    @GetMapping("/search")
    public List<MovieSummary> search(@Valid MovieSearchRequest request) {
        return movieService.search(request);
    }
}
