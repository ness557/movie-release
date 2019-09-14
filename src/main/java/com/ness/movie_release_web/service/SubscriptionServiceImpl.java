package com.ness.movie_release_web.service;

import com.ness.movie_release_web.dto.Language;
import com.ness.movie_release_web.dto.tmdb.movie.details.TmdbMovieDetailsDto;
import com.ness.movie_release_web.dto.tmdb.tvSeries.details.TmdbTVDetailsDto;
import com.ness.movie_release_web.model.*;
import com.ness.movie_release_web.repository.MovieRepository;
import com.ness.movie_release_web.repository.TVSeriesRepository;
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
    private final MovieRepository movieRepository;
    private final UserService userService;
    private final UserTVSeriesRepository userTVSeriesRepository;
    private final TmdbTVSeriesService tmdbTVSeriesService;
    private final TVSeriesRepository tvSeriesRepository;

    @Override
    public void subscribeToMovie(Long tmdbId, String login) {
        User user = userService.findByLogin(login);

        if (movieRepository.existsByIdAndUsers(tmdbId, user))
            throw new ResponseStatusException(HttpStatus.CONFLICT);

        Optional<TmdbMovieDetailsDto> optionalMovieDetails = tmdbMovieService.getMovieDetails(tmdbId, Language.en);

        if (!optionalMovieDetails.isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT);

        TmdbMovieDetailsDto tmdbMovieDetailsDto = optionalMovieDetails.get();

        Movie movie = movieRepository.findById(tmdbId).orElse(new Movie(tmdbMovieDetailsDto.getId(), tmdbMovieDetailsDto.getTitle(), "",
                tmdbMovieDetailsDto.getStatus(), tmdbMovieDetailsDto.getReleaseDate(),
                tmdbMovieDetailsDto.getVoteAverage().floatValue(), new ArrayList<>()));

        movie.getUsers().add(user);

        movieRepository.save(movie);
    }

    @Override
    public void unsubscribeFromMovie(Long tmdbId, String login) {
        User user = userService.findByLogin(login);

        movieRepository.findByIdAndUsers(tmdbId, user)
                .ifPresent(f -> {
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

        TmdbTVDetailsDto tvDetails = tmdbTVSeriesService.getTVDetails(tmdbId, Language.en)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT));
        TmdbTVDetailsDto tvDetailsRu = tmdbTVSeriesService.getTVDetails(tmdbId, Language.ru)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT));

        Optional<TVSeries> one = tvSeriesRepository.findById(tmdbId);

        UserTVSeries userTVSeries = new UserTVSeries();
        userTVSeries.setTvSeries(one.orElseGet(() -> {
            Long lastSeason = 0L;
            Long lastEpisode = 0L;
            if (tvDetails.getLastEpisodeToAir() != null) {
                lastSeason = tvDetails.getLastEpisodeToAir().getSeasonNumber();
                lastEpisode = tvDetails.getLastEpisodeToAir().getEpisodeNumber();
            }

            return new TVSeries(
                    tmdbId,
                    tvDetails.getName(),
                    tvDetailsRu.getName(),
                    tvDetails.getFirstAirDate(),
                    tvDetails.getLastAirDate(),
                    tvDetails.getVoteAverage(),
                    lastSeason,
                    lastEpisode,
                    tvDetails.getStatus()
            );
        }));
        userTVSeries.setUser(user);
        userTVSeries.setCurrentSeason(0L);
        userTVSeries.setCurrentEpisode(0L);

        userTVSeriesRepository.save(userTVSeries);
    }

    @Override
    public void unsubscribeFromSeries(Long tmdbId, String login) {
        User user = userService.findByLogin(login);

        UserTVSeries userTVSeries = userTVSeriesRepository.findById(UserTVSeriesPK.wrap(user.getId(), tmdbId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT));

        userTVSeriesRepository.delete(userTVSeries);
    }
}
