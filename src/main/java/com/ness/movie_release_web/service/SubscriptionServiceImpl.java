package com.ness.movie_release_web.service;

import com.ness.movie_release_web.model.*;
import com.ness.movie_release_web.model.dto.tmdb.Language;
import com.ness.movie_release_web.model.dto.tmdb.movie.details.MovieDetailsDto;
import com.ness.movie_release_web.model.dto.tmdb.tvSeries.details.TVDetailsDto;
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

        Optional<MovieDetailsDto> optionalMovieDetails = tmdbMovieService.getMovieDetails(tmdbId, Language.en);

        if (!optionalMovieDetails.isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT);

        MovieDetailsDto movieDetailsDto = optionalMovieDetails.get();

        Optional<Film> filmOpt = filmService.findById(tmdbId);

        Film film = filmOpt.orElse(new Film(movieDetailsDto.getId(), movieDetailsDto.getTitle(), "",
                movieDetailsDto.getStatus(), movieDetailsDto.getReleaseDate(),
                movieDetailsDto.getVoteAverage().floatValue(), new ArrayList<>()));
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

        Optional<TVDetailsDto> tvDetailsOptional = tmdbTVSeriesService.getTVDetails(tmdbId, Language.en);
        Optional<TVDetailsDto> tvDetailsOptionalRu = tmdbTVSeriesService.getTVDetails(tmdbId, Language.ru);

        if (!tvDetailsOptional.isPresent() || !tvDetailsOptionalRu.isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT);

        TVDetailsDto tvDetails = tvDetailsOptional.get();
        TVDetailsDto tvDetailsRu = tvDetailsOptionalRu.get();

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
