package com.ness.movie_release_web.service;

import com.ness.movie_release_web.model.Film;
import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.model.dto.tmdb.Language;
import com.ness.movie_release_web.model.dto.tmdb.movie.details.MovieDetailsDto;
import com.ness.movie_release_web.model.dto.tmdb.movie.details.Status;
import com.ness.movie_release_web.repository.FilmRepository;
import com.ness.movie_release_web.repository.MovieSortBy;
import com.ness.movie_release_web.service.tmdb.TmdbMovieService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.ness.movie_release_web.repository.FilmSpecifications.byUserAndStatusWithOrderby;

@Service
@Slf4j
public class FilmServiceImpl implements FilmService {

    @Autowired
    private FilmRepository repository;

    @Autowired
    private TmdbMovieService tmdbMovieService;

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
    public Optional<Film> getByTmdbIdAndUser(Long tmdbId, User user) {
        return repository.findByIdAndUsers(tmdbId, user);
    }

    @Override
    public boolean isExistsByTmdbIdAndUser(Long tmdbId, User user) {
        return repository.existsByIdAndUsers(tmdbId, user);
    }

    @Override
    public Page<Film> getByUserAndStatusWithOrderbyAndPages(List<Status> statuses,
                                                            MovieSortBy sortBy,
                                                            User user,
                                                            Integer page,
                                                            Integer size) {
        return repository.findAll(byUserAndStatusWithOrderby(statuses, sortBy, user), PageRequest.of(page, size));
    }

    @Override
    public void updateDB() {

        log.info("Updating film db...");
        repository.findAll().forEach(f -> {
            Long tmdbId = f.getId();
            Optional<MovieDetailsDto> movieDetails = tmdbMovieService.getMovieDetails(tmdbId, Language.en);

            if (!movieDetails.isPresent()) {
                return;
            }

            MovieDetailsDto movieDetailsDto = movieDetails.get();
            f.setReleaseDate(movieDetailsDto.getReleaseDate());
            f.setVoteAverage(movieDetailsDto.getVoteAverage().floatValue());
            f.setNameEn(movieDetailsDto.getTitle());
            f.setStatus(movieDetailsDto.getStatus());

            Optional<MovieDetailsDto> movieDetailsRuOpt = tmdbMovieService.getMovieDetails(tmdbId, Language.ru);
            movieDetailsRuOpt.ifPresent(movieDetailsRu -> f.setNameRu(movieDetailsRu.getTitle()));

            repository.save(f);
        });
        log.info("film db updated!");
    }
}
