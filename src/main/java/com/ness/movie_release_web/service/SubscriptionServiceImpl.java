package com.ness.movie_release_web.service;

import com.ness.movie_release_web.model.*;
import com.ness.movie_release_web.dto.Language;
import com.ness.movie_release_web.dto.tmdb.movie.details.TmdbMovieDetailsDto;
import com.ness.movie_release_web.dto.tmdb.tvSeries.details.TmdbTVDetailsDto;
import com.ness.movie_release_web.repository.FilmRepository;
import com.ness.movie_release_web.repository.UserTVSeriesRepository;
import com.ness.movie_release_web.service.tmdb.TmdbMovieService;
import com.ness.movie_release_web.service.tmdb.TmdbTVSeriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final TmdbMovieService tmdbMovieService;
    private final FilmRepository movieRepository;
    private final UserService userService;
    private final UserTVSeriesRepository userTVSeriesRepository;
    private final TmdbTVSeriesService tmdbTVSeriesService;
    private final TVSeriesService tvSeriesService;

    @Override
    public void subscribeToMovie(Long tmdbId, String login) {
        User user = userService.findByLogin(login);

        if (movieRepository.existsByIdAndUsers(tmdbId, user))
            throw new ResponseStatusException(HttpStatus.CONFLICT);

        Optional<TmdbMovieDetailsDto> optionalMovieDetails = tmdbMovieService.getMovieDetails(tmdbId, Language.en);

        if (!optionalMovieDetails.isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT);

        TmdbMovieDetailsDto tmdbMovieDetailsDto = optionalMovieDetails.get();

        Optional<Film> filmOpt = movieRepository.findById(tmdbId);

        Film film = filmOpt.orElse(new Film(tmdbMovieDetailsDto.getId(), tmdbMovieDetailsDto.getTitle(), "",
                tmdbMovieDetailsDto.getStatus(), tmdbMovieDetailsDto.getReleaseDate(),
                tmdbMovieDetailsDto.getVoteAverage().floatValue(), new ArrayList<>()));
        film.getUsers().add(user);

        movieRepository.save(film);
    }

    @Override
    public void unsubscribeFromMovie(Long tmdbId, String login) {
        User user = userService.findByLogin(login);

        Optional<Film> film = movieRepository.findByIdAndUsers(tmdbId, user);
        film.ifPresent(f -> {
            f.getUsers().remove(user);
            movieRepository.save(f);
        });
    }

    @Override
    public void subscribeToSeries(Long tmdbId, String login) {

        User user = userService.findByLogin(login);

        if (userTVSeriesRepository.existsById(UserTVSeriesPK.wrap(user.getId(), tmdbId))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        Optional<TmdbTVDetailsDto> tvDetailsOptional = tmdbTVSeriesService.getTVDetails(tmdbId, Language.en);
        Optional<TmdbTVDetailsDto> tvDetailsOptionalRu = tmdbTVSeriesService.getTVDetails(tmdbId, Language.ru);

        if (!tvDetailsOptional.isPresent() || !tvDetailsOptionalRu.isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT);

        TmdbTVDetailsDto tvDetails = tvDetailsOptional.get();
        TmdbTVDetailsDto tvDetailsRu = tvDetailsOptionalRu.get();

        Optional<TVSeries> one = tvSeriesService.findById(tmdbId);

        UserTVSeries userTVSeries = new UserTVSeries();
        userTVSeries.setTvSeries(one.orElse(new TVSeries(
                tmdbId,
                tvDetails.getName(),
                tvDetailsRu.getName(),
                tvDetails.getFirstAirDate(),
                tvDetails.getLastAirDate(),
                tvDetails.getVoteAverage(),
                0L,
                0L,
                tvDetails.getStatus()
        )));
        userTVSeries.setUser(user);
        userTVSeries.setCurrentSeason(0L);
        userTVSeries.setCurrentEpisode(0L);

        userTVSeriesRepository.save(userTVSeries);
    }

    @Override
    public void unsubscribeFromSeries(Long tmdbId, String login) {
        User user = userService.findByLogin(login);

        Optional<UserTVSeries> userTVSeries = userTVSeriesRepository.findById(UserTVSeriesPK.wrap(user.getId(), tmdbId));

        if (!userTVSeries.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        userTVSeriesRepository.delete(userTVSeries.get());
    }
}
