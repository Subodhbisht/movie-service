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
class SearchMoviesApiTest {

    @Autowired
    RestTestClient restTestClient;

    @Test
    @DisplayName("Should match title partially and case-insensitively")
    void shouldMatchTitlePartially() {
        var movies = search("/api/movies/search?title=KNIGHT");

        assertThat(movies)
                .extracting(MovieSummary::id)
                .containsExactly(1L);
    }

    @Test
    @DisplayName("Should treat percent sign in title as a literal character")
    void shouldEscapeWildcardsInTitle() {
        var movies = search("/api/movies/search?title={title}", "100%");

        assertThat(movies)
                .extracting(MovieSummary::id)
                .containsExactly(5L);
    }

    @Test
    @DisplayName("Should match genre exactly and sort by vote count")
    void shouldMatchGenreExactly() {
        var movies = search("/api/movies/search?genre=Action");

        assertThat(movies)
                .extracting(MovieSummary::id)
                .containsExactly(1L, 2L, 6L);
    }

    @Test
    @DisplayName("Should not match genre with different case")
    void shouldMatchGenreCaseSensitively() {
        assertThat(search("/api/movies/search?genre=action")).isEmpty();
    }

    @Test
    @DisplayName("Should combine title and genre with AND logic")
    void shouldCombineFilters() {
        var movies = search("/api/movies/search?title=love&genre=Drama");

        assertThat(movies)
                .extracting(MovieSummary::id)
                .containsExactly(5L);
    }

    @Test
    @DisplayName("Should apply the requested limit")
    void shouldApplyLimit() {
        var movies = search("/api/movies/search?genre=Action&limit=2");

        assertThat(movies)
                .extracting(MovieSummary::id)
                .containsExactly(1L, 2L);
    }

    @Test
    @DisplayName("Should return 400 ProblemDetail when no search parameter is provided")
    void shouldReturn400WhenNoParams() {
        assertBadRequest("/api/movies/search");
    }

    @Test
    @DisplayName("Should return 400 ProblemDetail when all search parameters are blank")
    void shouldReturn400WhenParamsBlank() {
        assertBadRequest("/api/movies/search?title={title}&genre=", "   ");
    }

    private List<MovieSummary> search(String uri, Object... uriVariables) {
        return restTestClient.get()
                .uri(uri, uriVariables)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<MovieSummary>>() {})
                .returnResult()
                .getResponseBody();
    }

    private void assertBadRequest(String uri, Object... uriVariables) {
        var problem = restTestClient.get()
                .uri(uri, uriVariables)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class)
                .returnResult()
                .getResponseBody();

        assertThat(problem.getStatus()).isEqualTo(400);
    }
}
