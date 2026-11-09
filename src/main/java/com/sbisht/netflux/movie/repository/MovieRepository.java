package com.sbisht.netflux.movie.repository;

import com.sbisht.netflux.movie.entity.Movie;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long>, JpaSpecificationExecutor<Movie> {

    List<Movie> findAllBy(Sort sort, Limit limit);

    List<Movie> findByReleaseDateLessThanEqual(LocalDate date, Sort sort, Limit limit);
}
