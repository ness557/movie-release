package com.ness.movie_release_web.repository;

import com.ness.movie_release_web.model.Film;
import com.ness.movie_release_web.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface FilmRepository extends JpaRepository<Film, Long> {
    List<Film> findAllByTmdbId(Integer tmdbId);
    List<Film> findAllByTmdbIdAndUserId(Integer tmdbId, Long userId);
    boolean existsByTmdbIdAndUserId(Integer tmdbId, Long userId);

    @Query("SELECT DISTINCT f.tmdbId FROM Film f")
    List<String> getUniqueTmdbIds();

    Page<Film> findAllByUserOrderBySubscriptionDateDesc(User user, Pageable pageable);
}
