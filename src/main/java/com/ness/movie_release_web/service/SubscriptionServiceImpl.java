package com.ness.movie_release_web.service;

import com.ness.movie_release_web.model.*;
import com.ness.movie_release_web.model.wrapper.tmdb.Language;
import com.ness.movie_release_web.model.wrapper.tmdb.movie.details.MovieDetailsWrapper;
import com.ness.movie_release_web.model.wrapper.tmdb.tvSeries.details.TVDetailsWrapper;
import com.ness.movie_release_web.repository.UserTVSeriesRepository;
import com.ness.movie_release_web.service.tmdb.TmdbMovieService;
import com.ness.movie_release_web.service.tmdb.TmdbTVSeriesService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private TmdbMovieService tmdbMovieService;
    private FilmService filmService;
    private UserService userService;
    private UserTVSeriesRepository userTVSeriesRepository;
    private TmdbTVSeriesService tmdbTVSeriesService;
    private TVSeriesService tvSeriesService;

    @Override
    public void subscribeToMovie(Long tmdbId, String login) {
        User user = userService.findByLogin(login);

        if (filmService.isExistsByTmdbIdAndUser(tmdbId, user))
            throw new ResponseStatusException(HttpStatus.CONFLICT);

        Optional<MovieDetailsWrapper> optionalMovieDetails = tmdbMovieService.getMovieDetails(tmdbId, Language.en);

        if (!optionalMovieDetails.isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT);

        MovieDetailsWrapper movieDetailsWrapper = optionalMovieDetails.get();

        Optional<Film> filmOpt = filmService.findById(tmdbId);

        Film film = filmOpt.orElse(new Film(movieDetailsWrapper.getId(), movieDetailsWrapper.getTitle(), "",
                movieDetailsWrapper.getStatus(), movieDetailsWrapper.getReleaseDate(),
                movieDetailsWrapper.getVoteAverage().floatValue(), new ArrayList<>()));
        film.getUsers().add(user);

        filmService.save(film);
    }

    @Override
    public void unsubscribeFromMovie(Long tmdbId, String login) {
        User user = userService.findByLogin(login);

        Optional<Film> film = filmService.getByTmdbIdAndUser(tmdbId, user);
        film.ifPresent(f -> {
            f.getUsers().remove(user);
            filmService.save(f);
        });
    }

    @Override
    public void subscribeToSeries(Long tmdbId, String login) {

        User user = userService.findByLogin(login);

        if (userTVSeriesRepository.existsById(UserTVSeriesPK.wrap(user.getId(), tmdbId))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        Optional<TVDetailsWrapper> tvDetailsOptional = tmdbTVSeriesService.getTVDetails(tmdbId, Language.en);
        Optional<TVDetailsWrapper> tvDetailsOptionalRu = tmdbTVSeriesService.getTVDetails(tmdbId, Language.ru);

        if (!tvDetailsOptional.isPresent() || !tvDetailsOptionalRu.isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT);

        TVDetailsWrapper tvDetails = tvDetailsOptional.get();
        TVDetailsWrapper tvDetailsRu = tvDetailsOptionalRu.get();

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
