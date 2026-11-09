package com.sbisht.netflux.movie;

import static org.assertj.core.api.Assertions.assertThat;

import com.sbisht.netflux.movie.dto.MovieSummary;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@AutoConfigureRestTestClient
@ActiveProfiles("test")
@Sql(scripts = "/sql/insert-movies.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/delete-movies.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class BatchMoviesApiTest {

    @Autowired
    RestTestClient restTestClient;

    @Test
    @DisplayName("Should return movies in request order, skipping missing and duplicate IDs")
    void shouldReturnMoviesInRequestOrder() {
        var movies = restTestClient.get()
                .uri("/api/movies/batch?ids={ids}", "3,999,1,3")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<MovieSummary>>() {})
                .returnResult()
                .getResponseBody();

        assertThat(movies)
                .extracting(MovieSummary::id)
                .containsExactly(3L, 1L);
        assertThat(movies.getFirst().title()).isEqualTo("Superbad");
    }

    @Test
    @DisplayName("Should return empty list when no IDs exist")
    void shouldReturnEmptyListWhenNoneFound() {
        var movies = restTestClient.get()
                .uri("/api/movies/batch?ids={ids}", "998,999")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<MovieSummary>>() {})
                .returnResult()
                .getResponseBody();

        assertThat(movies).isEmpty();
    }

    @Test
    @DisplayName("Should return 400 ProblemDetail when ids parameter is missing")
    void shouldReturn400WhenIdsMissing() {
        assertBadRequest("/api/movies/batch");
    }

    @Test
    @DisplayName("Should return 400 ProblemDetail when ids parameter is empty")
    void shouldReturn400WhenIdsEmpty() {
        assertBadRequest("/api/movies/batch?ids=");
    }

    @Test
    @DisplayName("Should return 400 ProblemDetail when an ID is not numeric")
    void shouldReturn400WhenIdNotNumeric() {
        assertBadRequest("/api/movies/batch?ids=1,abc");
    }

    @Test
    @DisplayName("Should return 400 ProblemDetail when more than 100 IDs are requested")
    void shouldReturn400WhenTooManyIds() {
        var ids = LongStream.rangeClosed(1, 101)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(","));

        assertBadRequest("/api/movies/batch?ids=" + ids);
    }

    private void assertBadRequest(String uri) {
        var problem = restTestClient.get()
                .uri(uri)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class)
                .returnResult()
                .getResponseBody();

        assertThat(problem.getStatus()).isEqualTo(400);
    }
}
