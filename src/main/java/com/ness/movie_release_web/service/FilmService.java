package com.ness.movie_release_web.service;

import com.ness.movie_release_web.model.Film;
import com.ness.movie_release_web.model.User;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface FilmService {
    
    void save(Film film);

    Film get(Long id);
    Optional<Film> findByTmdbId(Integer tmdbId);

    void delete(Film film);
    void delete(Long id);
    void delete(List<Film> films);

    List<Film> getAll();
    Page<Film> getAllByUserWithPages(Integer page, Integer size, User user);

    Optional<Film> getByTmdbIdAndUser(Integer tmdbId, User user);
    boolean isExistsByTmdbIdAndUser(Integer tmdbId, User user);
}
