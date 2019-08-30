package com.ness.movie_release_web.service.notification;

import com.ness.movie_release_web.dto.Language;
import com.ness.movie_release_web.dto.tmdb.movie.details.TmdbMovieDetailsDto;
import com.ness.movie_release_web.dto.tmdb.releaseDates.ReleaseType;
import com.ness.movie_release_web.dto.tmdb.releaseDates.TmdbReleaseDate;
import com.ness.movie_release_web.dto.tmdb.tvSeries.details.TmdbEpisodeDto;
import com.ness.movie_release_web.dto.tmdb.tvSeries.details.TmdbSeasonDto;
import com.ness.movie_release_web.dto.tmdb.tvSeries.details.TmdbTVDetailsDto;
import com.ness.movie_release_web.model.Film;
import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.model.UserTVSeries;
import com.ness.movie_release_web.repository.FilmRepository;
import com.ness.movie_release_web.service.TVSeriesService;
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

    private final FilmRepository movieRepository;
    private final TmdbMovieService tmdbMovieService;
    private final TelegramService telegramService;
    private final EmailService emailService;
    private final TmdbDatesService tmdbDatesService;
    private final TVSeriesService tvSeriesService;
    private final TmdbTVSeriesService tmdbTVSeriesService;

    @Override
    @Scheduled(cron = "${cron.pattern.notify.movie}")
    public void notifyForMovie() {

        log.info("Notifying for movies...");

        List<Film> films = movieRepository.findAll();

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(2);

        Map<User, Map<TmdbMovieDetailsDto, TmdbReleaseDate>> telegramNotifies = new HashMap<>();
        Map<User, Map<TmdbMovieDetailsDto, TmdbReleaseDate>> emailNotifies = new HashMap<>();

        // notifies about release
        films.forEach((f) -> {

            List<TmdbReleaseDate> releaseDates =
                    tmdbDatesService.getReleaseDates(
                            f.getId(),
                            ReleaseType.Theatrical,
                            ReleaseType.Digital,
                            ReleaseType.Physical);

            Map<Language, TmdbMovieDetailsDto> langMovie =
                    Arrays.stream(Language.values()).collect(toMap(l -> l, l -> tmdbMovieService.getMovieDetails(f.getId(), l).orElse(new TmdbMovieDetailsDto())));

            f.getUsers().forEach(u ->
                    releaseDates.forEach(rd -> {
                        if (rd.getReleaseDate().isBefore(endDate) && rd.getReleaseDate().isAfter(startDate)) {
                            if (u.isTelegramNotify()) {
                                addMovieNotify(langMovie.get(u.getLanguage()), u, rd, telegramNotifies);
                            } else {
                                addMovieNotify(langMovie.get(u.getLanguage()), u, rd, emailNotifies);
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
        List<UserTVSeries> allUserTVSeries = tvSeriesService.getAllUserTVSeries();

        // @formatter:off
        // for each value from db
        allUserTVSeries.forEach(tv -> {

            // get foll tv show info
            Optional<TmdbTVDetailsDto> tvDetails =
                    tmdbTVSeriesService.getTVDetails(tv.getId().getTvSeriesId(),
                                                     tv.getUser().getLanguage());
            if (tvDetails.isPresent()) {
                TmdbTVDetailsDto tmdbTvDetailsDto = tvDetails.get();

                // for each season of tv show
                tmdbTvDetailsDto.getSeasons().forEach(s -> {

                    // get all season info
                    Optional<TmdbSeasonDto> seasonDetails =
                            tmdbTVSeriesService.getSeasonDetails(tmdbTvDetailsDto.getId(),
                                                                 s.getSeasonNumber(),
                                                                 tv.getUser().getLanguage());
                    if (seasonDetails.isPresent()) {
                        TmdbSeasonDto tmdbSeasonDto = seasonDetails.get();


                        // if all episode dates matches season air date (season was released fully that date)
                        if (tmdbSeasonDto.getEpisodes().stream().allMatch(e -> e.getAirDate().equals(tmdbSeasonDto.getAirDate()))) {

                            // season air date matches condition
                            if (tmdbSeasonDto.getAirDate().isBefore(endDate) && tmdbSeasonDto.getAirDate().isAfter(startDate)) {

                                // notify about season
                                if (tv.getUser().isTelegramNotify()) {

                                    // add to telegram notify list
                                    addSeasonNotify(tmdbSeasonDto, tmdbTvDetailsDto, tv.getUser(), telegramSeasonNotifies);
                                } else {

                                    // add to email notify list
                                    addSeasonNotify(tmdbSeasonDto, tmdbTvDetailsDto, tv.getUser(), emailSeasonNotifies);
                                }
                            }

                        // season episodes DOES NOT matches season air date
                        } else {

                            // looking for episodes, the date of which matches condition
                            tmdbSeasonDto.getEpisodes().stream().filter(e -> e.getAirDate().isBefore(endDate) && e.getAirDate().isAfter(startDate)).forEach(e -> {

                                // notify about episode
                                if(tv.getUser().isTelegramNotify()){

                                    // add to telegram notify list
                                    addEpisodeNotify(e, tmdbTvDetailsDto, tv.getUser(), telegramEpisodeNotifies);
                                } else {

                                    // add to email notify list
                                    addEpisodeNotify(e, tmdbTvDetailsDto, tv.getUser(), emailEpisodeNotifies);
                                }
                            });
                        }
                    }
                });
            }
        });
        // @formatter:on

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
