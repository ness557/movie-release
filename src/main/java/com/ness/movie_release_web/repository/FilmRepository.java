package com.ness.movie_release_web.repository;

import com.ness.movie_release_web.model.Film;
import com.ness.movie_release_web.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
@Transactional
public interface FilmRepository extends JpaRepository<Film, Long>, JpaSpecificationExecutor<Film> {
    Optional<Film> findByTmdbIdAndUsers(Integer tmdbId, User user);
    boolean existsByTmdbIdAndUsers(Integer tmdbId, User user);

    Optional<Film> findByTmdbId(Integer tmdbId);
}
