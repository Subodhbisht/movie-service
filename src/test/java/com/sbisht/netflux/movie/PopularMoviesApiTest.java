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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@AutoConfigureRestTestClient
@ActiveProfiles("test")
@Sql(scripts = "/sql/insert-movies.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/delete-movies.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class PopularMoviesApiTest {

    @Autowired
    RestTestClient restTestClient;

    @Test
    @DisplayName("Should sort by vote count descending with nulls last and ties by ID")
    void shouldSortByVoteCount() {
        var movies = getPopular("/api/movies/popular");

        assertThat(movies)
                .extracting(MovieSummary::id)
                .containsExactly(1L, 2L, 3L, 5L, 7L, 6L, 4L);
    }

    @Test
    @DisplayName("Should apply the requested limit")
    void shouldApplyLimit() {
        var movies = getPopular("/api/movies/popular?limit=3");

        assertThat(movies)
                .extracting(MovieSummary::id)
                .containsExactly(1L, 2L, 3L);
    }

    @Test
    @DisplayName("Should return 20 movies by default")
    @Sql(scripts = "/sql/insert-many-movies.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/delete-movies.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldUseDefaultLimit() {
        assertThat(getPopular("/api/movies/popular")).hasSize(20);
    }

    @Test
    @DisplayName("Should clamp limit above 100 to 100")
    @Sql(scripts = "/sql/insert-many-movies.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/delete-movies.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldClampLimitToMax() {
        assertThat(getPopular("/api/movies/popular?limit=500")).hasSize(100);
    }

    @Test
    @DisplayName("Should fall back to default limit when limit is zero or negative")
    @Sql(scripts = "/sql/insert-many-movies.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/delete-movies.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldUseDefaultLimitWhenNotPositive() {
        assertThat(getPopular("/api/movies/popular?limit=0")).hasSize(20);
        assertThat(getPopular("/api/movies/popular?limit=-5")).hasSize(20);
    }

    @Test
    @DisplayName("Should return 400 ProblemDetail when limit is not numeric")
    void shouldReturn400WhenLimitNotNumeric() {
        var problem = restTestClient.get()
                .uri("/api/movies/popular?limit=abc")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class)
                .returnResult()
                .getResponseBody();

        assertThat(problem.getStatus()).isEqualTo(400);
    }

    private List<MovieSummary> getPopular(String uri) {
        return restTestClient.get()
                .uri(uri)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<MovieSummary>>() {})
                .returnResult()
                .getResponseBody();
    }
}
