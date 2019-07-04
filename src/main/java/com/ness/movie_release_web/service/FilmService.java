package com.ness.movie_release_web.service;

import com.ness.movie_release_web.model.Film;
import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.model.dto.tmdb.movie.details.Status;
import com.ness.movie_release_web.repository.MovieSortBy;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface FilmService {
    
    void save(Film film);

    Film get(Long id);
    Optional<Film> findById(Long tmdbId);

    void delete(Film film);
    void delete(List<Film> films);

    List<Film> getAll();

    Optional<Film> getByTmdbIdAndUser(Long tmdbId, User user);
    boolean isExistsByTmdbIdAndUser(Long tmdbId, User user);

    Page<Film> getByUserAndStatusWithOrderbyAndPages(List<Status> statuses,
                                                  MovieSortBy sortBy,
                                                  User user,
                                                  Integer page,
                                                  Integer size);

    void updateDB();
}
