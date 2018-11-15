package com.ness.movie_release_web.repository;

import com.ness.movie_release_web.model.Film;
import com.ness.movie_release_web.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface FilmRepository extends JpaRepository<Film, Long>, JpaSpecificationExecutor<Film> {
    List<Film> findAllByTmdbId(Integer tmdbId);
    Optional<Film> findByTmdbIdAndUsers(Integer tmdbId, User user);
    boolean existsByTmdbIdAndUsers(Integer tmdbId, User user);

    Optional<Film> findByTmdbId(Integer tmdbId);

    @Query("SELECT DISTINCT f.tmdbId FROM Film f")
    List<String> getUniqueTmdbIds();

    Page<Film> findAllByUsers(User user, Pageable pageable);
}
