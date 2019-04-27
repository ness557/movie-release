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
    public void subscribeToMovie(Integer tmdbId, String login) {
        User user = userService.findByLogin(login);

        if (filmService.isExistsByTmdbIdAndUser(tmdbId, user))
            throw new ResponseStatusException(HttpStatus.CONFLICT);

        Optional<MovieDetailsWrapper> optionalMovieDetails = tmdbMovieService.getMovieDetails(tmdbId, Language.en);

        if (!optionalMovieDetails.isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT);

        MovieDetailsWrapper movieDetailsWrapper = optionalMovieDetails.get();

        Optional<Film> filmOpt = filmService.findById(tmdbId.longValue());

        Film film = filmOpt.orElse(new Film(movieDetailsWrapper.getId().longValue(), movieDetailsWrapper.getTitle(), "",
                movieDetailsWrapper.getStatus(), movieDetailsWrapper.getReleaseDate(),
                movieDetailsWrapper.getVoteAverage().floatValue(), new ArrayList<>()));
        film.getUsers().add(user);

        filmService.save(film);
    }

    @Override
    public void unsubscribeFromMovie(Integer tmdbId, String login) {
        User user = userService.findByLogin(login);

        Optional<Film> film = filmService.getByTmdbIdAndUser(tmdbId, user);
        film.ifPresent(f -> {
            f.getUsers().remove(user);
            filmService.save(f);
        });
    }

    @Override
    public void subscribeToSeries(Integer tmdbId, String login) {

        User user = userService.findByLogin(login);

        if (userTVSeriesRepository.existsById(UserTVSeriesPK.wrap(user.getId(), tmdbId.longValue()))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        Optional<TVDetailsWrapper> tvDetailsOptional = tmdbTVSeriesService.getTVDetails(tmdbId, Language.en);
        Optional<TVDetailsWrapper> tvDetailsOptionalRu = tmdbTVSeriesService.getTVDetails(tmdbId, Language.ru);

        if (!tvDetailsOptional.isPresent() || !tvDetailsOptionalRu.isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT);

        TVDetailsWrapper tvDetails = tvDetailsOptional.get();
        TVDetailsWrapper tvDetailsRu = tvDetailsOptionalRu.get();

        Optional<TVSeries> one = tvSeriesService.findById(tmdbId.longValue());

        UserTVSeries userTVSeries = new UserTVSeries();
        userTVSeries.setTvSeries(one.orElse(new TVSeries(
                tmdbId.longValue(),
                tvDetails.getName(),
                tvDetailsRu.getName(),
                tvDetails.getFirstAirDate(),
                tvDetails.getLastAirDate(),
                tvDetails.getVoteAverage(),
                0,
                0,
                tvDetails.getStatus()
        )));
        userTVSeries.setUser(user);
        userTVSeries.setCurrentSeason(0);
        userTVSeries.setCurrentEpisode(0);

        userTVSeriesRepository.save(userTVSeries);
    }

    @Override
    public void unsubscribeFromSeries(Integer tmdbId, String login) {
        User user = userService.findByLogin(login);

        Optional<UserTVSeries> userTVSeries = userTVSeriesRepository.findById(UserTVSeriesPK.wrap(user.getId(), tmdbId.longValue()));

        if (!userTVSeries.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        userTVSeriesRepository.delete(userTVSeries.get());
    }
}
