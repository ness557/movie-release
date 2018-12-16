package com.ness.movie_release_web.service;

import com.ness.movie_release_web.model.Film;
import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.model.wrapper.tmdb.movie.details.Status;
import com.ness.movie_release_web.repository.FilmRepository;
import com.ness.movie_release_web.repository.MovieSortBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.ness.movie_release_web.repository.FilmSpecifications.byUserAndStatusWithOrderby;

@Service
public class FilmServiceImpl implements FilmService {

    @Autowired
    private FilmRepository repository;

    @Override
    public void save(Film film) {
        repository.save(film);
    }


    @Override
    public Film get(Long id) {
        return repository.getOne(id);
    }

    @Override
    public Optional<Film> findById(Long tmdbId) {
        return repository.findById(tmdbId);
    }

    @Override
    public void delete(Film film) {
        repository.delete(film);
    }

    @Override
    public void delete(List<Film> films) {
        repository.deleteAll(films);
    }

    @Override
    public List<Film> getAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Film> getByTmdbIdAndUser(Integer tmdbId, User user) {
        return repository.findByIdAndUsers(tmdbId.longValue(), user);
    }

    @Override
    public boolean isExistsByTmdbIdAndUser(Integer tmdbId, User user) {
        return repository.existsByIdAndUsers(tmdbId.longValue(), user);
    }

    @Override
    public Page<Film> getByUserAndStatusWithOrderbyAndPages(List<Status> statuses,
                                                            MovieSortBy sortBy,
                                                            User user,
                                                            Integer page,
                                                            Integer size) {
        return repository.findAll(byUserAndStatusWithOrderby(statuses, sortBy, user), PageRequest.of(page, size));
    }
}
