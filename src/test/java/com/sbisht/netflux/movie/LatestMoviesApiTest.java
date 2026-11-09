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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@AutoConfigureRestTestClient
@ActiveProfiles("test")
@Sql(scripts = "/sql/insert-movies.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/delete-movies.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class LatestMoviesApiTest {

    @Autowired
    RestTestClient restTestClient;

    @Test
    @DisplayName("Should sort by release date descending excluding null and future dates")
    void shouldSortByReleaseDate() {
        var movies = getLatest("/api/movies/latest");

        assertThat(movies)
                .extracting(MovieSummary::id)
                .containsExactly(5L, 7L, 2L, 1L, 3L);
    }

    @Test
    @DisplayName("Should apply the requested limit")
    void shouldApplyLimit() {
        var movies = getLatest("/api/movies/latest?limit=2");

        assertThat(movies)
                .extracting(MovieSummary::id)
                .containsExactly(5L, 7L);
    }

    @Test
    @DisplayName("Should clamp limit above 100 to 100")
    @Sql(scripts = "/sql/insert-many-movies.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/delete-movies.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldClampLimitToMax() {
        assertThat(getLatest("/api/movies/latest?limit=500")).hasSize(100);
    }

    private List<MovieSummary> getLatest(String uri) {
        return restTestClient.get()
                .uri(uri)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<MovieSummary>>() {})
                .returnResult()
                .getResponseBody();
    }
}
