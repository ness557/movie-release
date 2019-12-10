package com.ness.movie_release_web.service.notification;

import com.ness.movie_release_web.dto.Language;
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
import java.util.*;

import static java.util.stream.Collectors.toMap;

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

        List<Movie> movies = movieRepository.findAll();

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(2);

        Map<User, Map<TmdbMovieDetailsDto, TmdbReleaseDate>> telegramNotifies = new HashMap<>();
        Map<User, Map<TmdbMovieDetailsDto, TmdbReleaseDate>> emailNotifies = new HashMap<>();

        // notifies about release
        movies.forEach((movie) -> {

            List<TmdbReleaseDate> releaseDates =
                    tmdbDatesService.getReleaseDates(
                            movie.getId(),
                            ReleaseType.Theatrical,
                            ReleaseType.Digital,
                            ReleaseType.Physical);

            Map<Language, TmdbMovieDetailsDto> langMovie =
                    Arrays.stream(Language.values())
                            .collect(toMap(
                                    language -> language,
                                    language -> tmdbMovieService.getMovieDetails(movie.getId(),
                                            language).orElse(new TmdbMovieDetailsDto())));

            movie.getUsers().forEach(u ->
                    releaseDates.forEach(releaseDate -> {
                        if (releaseDate.getReleaseDate().isBefore(endDate) && releaseDate.getReleaseDate().isAfter(startDate)) {

                            switch (u.getMessageDestinationType()) {
                                case EMAIL:
                                    addMovieNotify(langMovie.get(u.getLanguage()), u, releaseDate, emailNotifies);
                                    break;
                                case TELEGRAM:
                                    addMovieNotify(langMovie.get(u.getLanguage()), u, releaseDate, telegramNotifies);
                                    break;
                            }
                        }
                    }));
        });

        notifyMovieByEmail(emailNotifies);
        notifyMovieByTelegram(telegramNotifies);
        log.info("Notified by email: {}", emailNotifies.toString());
        log.info("Notified by telegram: {}", telegramNotifies.toString());
    }

    @Override
    @Scheduled(cron = "${cron.pattern.notify.series}")
    public void notifyForSeries() {

        log.info("Notifying for series...");

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(2);


        Map<User, Map<TmdbSeasonDto, TmdbTVDetailsDto>> telegramSeasonNotifies = new HashMap<>();
        Map<User, Map<TmdbSeasonDto, TmdbTVDetailsDto>> emailSeasonNotifies = new HashMap<>();
        Map<User, Map<TmdbEpisodeDto, TmdbTVDetailsDto>> emailEpisodeNotifies = new HashMap<>();
        Map<User, Map<TmdbEpisodeDto, TmdbTVDetailsDto>> telegramEpisodeNotifies = new HashMap<>();

        // get all tv shows from db
        List<UserTVSeries> allUserTVSeries = tvSeriesRepository.findAll();

        // for each value from db
        allUserTVSeries.forEach(userTvSeries -> {

            // get full userTvSeries show info
            Optional<TmdbTVDetailsDto> tvDetails =
                    tmdbTVSeriesService.getTVDetails(userTvSeries.getId().getTvSeriesId(), userTvSeries.getUser().getLanguage());

            tvDetails.ifPresent(tmdbTVDetailsDto -> {

                // for each season of userTvSeries show
                tmdbTVDetailsDto.getSeasons().forEach(s -> {

                    // get all season info
                    Optional<TmdbSeasonDto> seasonDetails =
                            tmdbTVSeriesService.getSeasonDetails(tmdbTVDetailsDto.getId(), s.getSeasonNumber(), userTvSeries.getUser().getLanguage());
                    if (seasonDetails.isPresent()) {
                        TmdbSeasonDto tmdbSeasonDto = seasonDetails.get();


                        // if all episode dates matches season air date (season was released fully that date)
                        if (tmdbSeasonDto.getEpisodes().stream().allMatch(e -> e.getAirDate().equals(tmdbSeasonDto.getAirDate()))
                                && tmdbSeasonDto.getEpisodes().size() > 1) {

                            // season air date matches condition
                            if (tmdbSeasonDto.getAirDate().isBefore(endDate) && tmdbSeasonDto.getAirDate().isAfter(startDate)) {

                                // notify about season
                                switch (userTvSeries.getUser().getMessageDestinationType()) {
                                    case EMAIL:
                                        // add to email notify list
                                        addSeasonNotify(tmdbSeasonDto, tmdbTVDetailsDto, userTvSeries.getUser(), emailSeasonNotifies);
                                        break;
                                    case TELEGRAM:
                                        // add to telegram notify list
                                        addSeasonNotify(tmdbSeasonDto, tmdbTVDetailsDto, userTvSeries.getUser(), telegramSeasonNotifies);
                                        break;
                                }
                            }

                            // season episodes DOES NOT matches season air date
                        } else {

                            // looking for episodes, the date of which matches condition
                            tmdbSeasonDto.getEpisodes().stream().filter(e -> e.getAirDate().isBefore(endDate) && e.getAirDate().isAfter(startDate)).forEach(e -> {
                                switch (userTvSeries.getUser().getMessageDestinationType()) {
                                    case EMAIL:
                                        // add to email notify list
                                        addEpisodeNotify(e, tmdbTVDetailsDto, userTvSeries.getUser(), emailEpisodeNotifies);
                                        break;
                                    case TELEGRAM:
                                        // add to telegram notify list
                                        addEpisodeNotify(e, tmdbTVDetailsDto, userTvSeries.getUser(), telegramEpisodeNotifies);
                                        break;
                                }
                            });
                        }
                    }
                });
            });
        });

        //notifying
        notifySeasonByEmail(emailSeasonNotifies);
        notifySeasonByTelegram(telegramSeasonNotifies);
        notifyEpisodeByEmail(emailEpisodeNotifies);
        notifyEpisodeByTelegram(telegramEpisodeNotifies);

        log.info("Notified episodes by email: {}", emailEpisodeNotifies.toString());
        log.info("Notified episodes by telegram: {}", telegramEpisodeNotifies.toString());
        log.info("Notified seasons by email: {}", emailSeasonNotifies.toString());
        log.info("Notified seasons by telegram: {}", telegramSeasonNotifies.toString());
    }


    private void addEpisodeNotify(TmdbEpisodeDto e, TmdbTVDetailsDto t, User user, Map<User, Map<TmdbEpisodeDto, TmdbTVDetailsDto>> notifies) {
        notifies.computeIfAbsent(user, k -> new HashMap<>());
        notifies.get(user).put(e, t);
    }

    private void addSeasonNotify(TmdbSeasonDto s, TmdbTVDetailsDto t, User user, Map<User, Map<TmdbSeasonDto, TmdbTVDetailsDto>> notifies) {
        notifies.computeIfAbsent(user, k -> new HashMap<>());
        notifies.get(user).put(s, t);
    }

    private void addMovieNotify(TmdbMovieDetailsDto movie, User user, TmdbReleaseDate releaseDate, Map<User, Map<TmdbMovieDetailsDto, TmdbReleaseDate>> notifies) {
        notifies.computeIfAbsent(user, k -> new HashMap<>());
        notifies.get(user).put(movie, releaseDate);
    }

    private void notifyMovieByEmail(Map<User, Map<TmdbMovieDetailsDto, TmdbReleaseDate>> notifies) {
        notifies.forEach((user, value) -> value.forEach((movie, rd) -> emailService.sendMovieNotify(user, movie, rd)));
    }

    private void notifyMovieByTelegram(Map<User, Map<TmdbMovieDetailsDto, TmdbReleaseDate>> notifies) {
        notifies.forEach((user, value) -> value.forEach((movie, rd) -> telegramService.sendMovieNotify(user, movie, rd)));
    }

    private void notifyEpisodeByTelegram(Map<User, Map<TmdbEpisodeDto, TmdbTVDetailsDto>> notifies) {
        notifies.forEach((user, value) -> value.forEach((episode, show) -> telegramService.sendEpisodeNotify(user, episode, show)));
    }

    private void notifyEpisodeByEmail(Map<User, Map<TmdbEpisodeDto, TmdbTVDetailsDto>> notifies) {
        notifies.forEach((user, value) -> value.forEach((episode, show) -> emailService.sendEpisodeNotify(user, episode, show)));
    }

    private void notifySeasonByTelegram(Map<User, Map<TmdbSeasonDto, TmdbTVDetailsDto>> notifies) {
        notifies.forEach((user, value) -> value.forEach((season, show) -> telegramService.sendSeasonNotify(user, season, show)));
    }

    private void notifySeasonByEmail(Map<User, Map<TmdbSeasonDto, TmdbTVDetailsDto>> notifies) {
        notifies.forEach((user, value) -> value.forEach((season, show) -> emailService.sendSeasonNotify(user, season, show)));
    }
}
