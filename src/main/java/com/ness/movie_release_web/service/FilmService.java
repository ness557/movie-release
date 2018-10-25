package com.ness.movie_release_web.service;

import com.ness.movie_release_web.model.Film;
import com.ness.movie_release_web.model.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface FilmService {
    
    void save(Film film);

    Film get(Long id);
    void delete(Film film);
    void delete(Long id);
    void delete(List<Film> films);

    List<Film> getAll();
    Page<Film> getAllByUserWithPages(Integer page, Integer size, User user);

    List<Film> getByTmdbIdAndUserId(Integer tmdbId, Long userId);
    boolean isExistsByTmdbIdAndUserId(Integer tmdbId, Long userId);
}
