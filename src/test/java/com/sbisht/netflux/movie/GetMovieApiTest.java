package com.sbisht.netflux.movie;

import static org.assertj.core.api.Assertions.assertThat;

import com.sbisht.netflux.movie.dto.MovieDetails;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@AutoConfigureRestTestClient
@ActiveProfiles("test")
@Sql(scripts = "/sql/insert-movies.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/delete-movies.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class GetMovieApiTest {

    @Autowired
    RestTestClient restTestClient;

    @Test
    @DisplayName("Should return full movie details for an existing ID")
    void shouldReturnMovieDetails() {
        var movie = restTestClient.get()
                .uri("/api/movies/{id}", 1)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MovieDetails.class)
                .returnResult()
                .getResponseBody();

        assertThat(movie).isEqualTo(new MovieDetails(
                1L,
                "The Dark Knight",
                8.5,
                5000,
                LocalDate.of(2008, 7, 18),
                1004558444L,
                152,
                "/dk-backdrop.jpg",
                185000000L,
                "https://example.com/dark-knight",
                "Batman faces the Joker.",
                120.5,
                "/dk-poster.jpg",
                List.of("Action", "Drama"),
                List.of("Heroism")
        ));
    }

    @Test
    @DisplayName("Should return empty lists when genres and themes are null")
    void shouldReturnEmptyListsForNullArrays() {
        var movie = restTestClient.get()
                .uri("/api/movies/{id}", 4)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MovieDetails.class)
                .returnResult()
                .getResponseBody();

        assertThat(movie.genres()).isEmpty();
        assertThat(movie.themes()).isEmpty();
    }

    @Test
    @DisplayName("Should return 404 ProblemDetail when ID does not exist")
    void shouldReturn404WhenNotFound() {
        var problem = restTestClient.get()
                .uri("/api/movies/{id}", 999)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ProblemDetail.class)
                .returnResult()
                .getResponseBody();

        assertThat(problem.getStatus()).isEqualTo(404);
        assertThat(problem.getDetail()).isEqualTo("Movie not found: 999");
    }

    @Test
    @DisplayName("Should return 400 ProblemDetail when ID is not numeric")
    void shouldReturn400WhenIdNotNumeric() {
        var problem = restTestClient.get()
                .uri("/api/movies/{id}", "abc")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class)
                .returnResult()
                .getResponseBody();

        assertThat(problem.getStatus()).isEqualTo(400);
    }
}
