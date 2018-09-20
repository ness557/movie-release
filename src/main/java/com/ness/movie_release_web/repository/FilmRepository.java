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
    Film findByTitle(String title);
    List<Film> findAllByImdbId(String imdbId);
    List<Film> findAllByImdbIdAndUserId(String imdbId, Long userId);
    boolean existsByImdbIdAndUserId(String imdbId, Long userId);

    @Query("SELECT DISTINCT f.imdbId FROM Film f")
    List<String> getUniqueImdbIds();

    Page<Film> findAllByUserOrderBySubscriptionDateDesc(User user, Pageable pageable);
}
