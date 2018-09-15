package com.ness.movie_release_web.service;

import com.ness.movie_release_web.model.Film;

import java.util.List;

public interface FilmService {
    
    void save(Film film);
    Film get(String title);
    Film get(Long id);
    void delete(Film film);
    void delete(Long id);
    void delete(List<Film> films);
    List<Film> getAll();
    List<Film> getByImdbIdAndUserId(String imdbId, Long userId);
    boolean isExistsByImdbIdAndUserId(String imdbId, Long userId);

}
