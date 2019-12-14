package com.ness.movie_release_web.service.notification;

import com.ness.movie_release_web.dto.tmdb.movie.details.TmdbMovieDetailsDto;
import com.ness.movie_release_web.dto.tmdb.releaseDates.ReleaseType;
import com.ness.movie_release_web.dto.tmdb.releaseDates.TmdbReleaseDate;
import com.ness.movie_release_web.dto.tmdb.tvSeries.details.TmdbEpisodeDto;
import com.ness.movie_release_web.dto.tmdb.tvSeries.details.TmdbSeasonDto;
import com.ness.movie_release_web.dto.tmdb.tvSeries.details.TmdbTVDetailsDto;
import com.ness.movie_release_web.model.Movie;
import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.model.UserTVSeries;
import com.ness.movie_release_web.repository.MovieRepository;
import com.ness.movie_release_web.repository.UserTVSeriesRepository;
import com.ness.movie_release_web.service.email.EmailService;
import com.ness.movie_release_web.service.telegram.TelegramService;
import com.ness.movie_release_web.service.tmdb.TmdbDatesService;
import com.ness.movie_release_web.service.tmdb.TmdbMovieService;
import com.ness.movie_release_web.service.tmdb.TmdbTVSeriesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final MovieRepository movieRepository;
    private final TmdbMovieService tmdbMovieService;
    private final TelegramService telegramService;
    private final EmailService emailService;
    private final TmdbDatesService tmdbDatesService;
    private final UserTVSeriesRepository tvSeriesRepository;
    private final TmdbTVSeriesService tmdbTVSeriesService;

    @Override
    @Scheduled(cron = "${cron.pattern.notify.movie}")
    public void notifyForMovie() {

        log.info("Notifying for movies...");

        List<Movie> movies = movieRepository.findAllByUsersIsNotEmpty();

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(2);

        Map<User, Map<TmdbMovieDetailsDto, TmdbReleaseDate>> notifies = new HashMap<>();

        // notifies about release
        movies.forEach(movie -> {

            List<TmdbReleaseDate> releaseDates =
                    tmdbDatesService.getReleaseDates(
                            movie.getId(),
                            ReleaseType.Theatrical,
                            ReleaseType.Digital,
                            ReleaseType.Physical);

            movie.getUsers().forEach(user ->
                    releaseDates.forEach(releaseDate -> {
                        if (releaseDate.getReleaseDate().isBefore(endDate) && releaseDate.getReleaseDate().isAfter(startDate)) {

                            tmdbMovieService.getMovieDetails(movie.getId(), user.getLanguage())
                                    .ifPresent(movieDetailsDto -> {
                                        notifies.computeIfAbsent(user, k -> new HashMap<>());
                                        notifies.get(user).put(movieDetailsDto, releaseDate);
                                    });
                        }
                    }));
        });

        notifies.forEach((user, detailsReleaseDateMap) -> detailsReleaseDateMap.forEach((movie, releaseDate) -> {
            try {
                switch (user.getMessageDestinationType()) {

                    case EMAIL:
                        emailService.sendMovieNotify(user, movie, releaseDate);
                        break;
                    case TELEGRAM:
                        telegramService.sendMovieNotify(user, movie, releaseDate);
                        break;
                }
            } catch (Exception e) {
                log.error("Couldn't notify for new season: ", e);
            }
        }));

        log.info("Notified {}", notifies);
    }

    @Override
    @Scheduled(cron = "${cron.pattern.notify.series}")
    public void notifyForSeries() {

        log.info("Notifying for series...");

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(2);

        Map<User, Map<TmdbSeasonDto, TmdbTVDetailsDto>> seasonNotifies = new HashMap<>();
        Map<User, Map<TmdbEpisodeDto, TmdbTVDetailsDto>> episodeNotifies = new HashMap<>();

        // get all tv shows from db
        List<UserTVSeries> allUserTVSeries = tvSeriesRepository.findAll();

        // for each value from db
        allUserTVSeries.forEach(userTvSeries -> {

            User user = userTvSeries.getUser();

            // get full userTvSeries show info
            Optional<TmdbTVDetailsDto> tvDetails =
                    tmdbTVSeriesService.getTVDetails(userTvSeries.getId().getTvSeriesId(), user.getLanguage());

            tvDetails.ifPresent(tmdbTVDetailsDto -> {

                // for each season of userTvSeries show
                tmdbTVDetailsDto.getSeasons().forEach(s -> {

                    // get all season info
                    Optional<TmdbSeasonDto> seasonDetails =
                            tmdbTVSeriesService.getSeasonDetails(tmdbTVDetailsDto.getId(), s.getSeasonNumber(), user.getLanguage());
                    if (seasonDetails.isPresent()) {
                        TmdbSeasonDto tmdbSeasonDto = seasonDetails.get();

                        // if all episode dates matches season air date (season was released fully that date)
                        if (tmdbSeasonDto.getEpisodes().stream().allMatch(e -> e.getAirDate().equals(tmdbSeasonDto.getAirDate()))
                                && tmdbSeasonDto.getEpisodes().size() > 1) {

                            // season air date matches condition
                            if (tmdbSeasonDto.getAirDate().isBefore(endDate) && tmdbSeasonDto.getAirDate().isAfter(startDate)) {
                                // notify about season
                                seasonNotifies.computeIfAbsent(user, k -> new HashMap<>());
                                seasonNotifies.get(user).put(tmdbSeasonDto, tmdbTVDetailsDto);
                            }

                            // season episodes DOES NOT matches season air date
                        } else {

                            // looking for episodes, the date of which matches condition
                            tmdbSeasonDto.getEpisodes().stream()
                                    .filter(episode -> episode.getAirDate().isBefore(endDate) && episode.getAirDate().isAfter(startDate))
                                    .forEach(episode -> {
                                        episodeNotifies.computeIfAbsent(user, k -> new HashMap<>());
                                        episodeNotifies.get(user).put(episode, tmdbTVDetailsDto);
                                    });
                        }
                    }
                });
            });
        });

        seasonNotifies.forEach((user, seasonToDetailsMap) -> seasonToDetailsMap.forEach((season, tvDetails) -> {
            try {
                switch (user.getMessageDestinationType()) {
                    case TELEGRAM:
                        telegramService.sendSeasonNotify(user, season, tvDetails);
                        break;
                    case EMAIL:
                        emailService.sendSeasonNotify(user, season, tvDetails);
                        break;
                }
            } catch (Exception e) {
                log.error("Couldn't notify for new season: ", e);
            }
        }));

        episodeNotifies.forEach((user, episodeToDetailsMap) -> episodeToDetailsMap.forEach((episode, tvDetails) -> {
            try {
                switch (user.getMessageDestinationType()) {
                    case TELEGRAM:
                        telegramService.sendEpisodeNotify(user, episode, tvDetails);
                        break;
                    case EMAIL:
                        emailService.sendEpisodeNotify(user, episode, tvDetails);
                        break;
                }
            } catch (Exception e) {
                log.error("Couldn't notify for new season: ", e);
            }
        }));

        log.info("Notified episodes: {}", episodeNotifies);
        log.info("Notified seasons: {}", seasonNotifies);
    }
}
