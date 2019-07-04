package com.ness.movie_release_web.service.notification;

import com.ness.movie_release_web.model.Film;
import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.model.UserTVSeries;
import com.ness.movie_release_web.model.dto.tmdb.Language;
import com.ness.movie_release_web.model.dto.tmdb.movie.details.MovieDetailsDto;
import com.ness.movie_release_web.model.dto.tmdb.releaseDates.ReleaseDate;
import com.ness.movie_release_web.model.dto.tmdb.releaseDates.ReleaseType;
import com.ness.movie_release_web.model.dto.tmdb.tvSeries.details.EpisodeDto;
import com.ness.movie_release_web.model.dto.tmdb.tvSeries.details.SeasonDto;
import com.ness.movie_release_web.model.dto.tmdb.tvSeries.details.TVDetailsDto;
import com.ness.movie_release_web.service.FilmService;
import com.ness.movie_release_web.service.TVSeriesService;
import com.ness.movie_release_web.service.email.EmailService;
import com.ness.movie_release_web.service.telegram.TelegramService;
import com.ness.movie_release_web.service.tmdb.TmdbDatesService;
import com.ness.movie_release_web.service.tmdb.TmdbMovieService;
import com.ness.movie_release_web.service.tmdb.TmdbTVSeriesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

import static java.util.stream.Collectors.toMap;

@Service
public class NotificationServiceImpl implements NotificationService {

    private FilmService filmService;
    private TmdbMovieService tmdbMovieService;
    private TelegramService telegramService;
    private EmailService emailService;
    private TmdbDatesService tmdbDatesService;
    private TVSeriesService tvSeriesService;
    private TmdbTVSeriesService tmdbTVSeriesService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public NotificationServiceImpl(FilmService filmService,
                                   TmdbMovieService tmdbMovieService,
                                   TelegramService telegramService,
                                   EmailService emailService,
                                   TmdbDatesService tmdbDatesService,
                                   TVSeriesService tvSeriesService,
                                   TmdbTVSeriesService tmdbTVSeriesService) {
        this.filmService = filmService;
        this.telegramService = telegramService;
        this.emailService = emailService;
        this.tmdbMovieService = tmdbMovieService;
        this.tmdbDatesService = tmdbDatesService;
        this.tvSeriesService = tvSeriesService;
        this.tmdbTVSeriesService = tmdbTVSeriesService;
    }

    @Override
    @Scheduled(cron = "${cron.pattern.notify.movie}")
    public void notifyForMovie() {

        logger.info("Notifying for movies...");

        List<Film> films = filmService.getAll();

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(2);

        Map<User, Map<MovieDetailsDto, ReleaseDate>> telegramNotifies = new HashMap<>();
        Map<User, Map<MovieDetailsDto, ReleaseDate>> emailNotifies = new HashMap<>();

        // notifies about release
        films.forEach((f) -> {

            List<ReleaseDate> releaseDates =
                    tmdbDatesService.getReleaseDates(
                            f.getId(),
                            ReleaseType.Theatrical,
                            ReleaseType.Digital,
                            ReleaseType.Physical);

            Map<Language, MovieDetailsDto> langMovie =
                    Arrays.stream(Language.values()).collect(toMap(l -> l, l -> tmdbMovieService.getMovieDetails(f.getId(), l).orElse(new MovieDetailsDto())));

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
        logger.info("Notified by email: {}", emailNotifies.toString());
        logger.info("Notified by telegram: {}", telegramNotifies.toString());
    }

    @Override
    @Scheduled(cron = "${cron.pattern.notify.series}")
    public void notifyForSeries() {

        logger.info("Notifying for series...");

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(2);


        Map<User, Map<SeasonDto, TVDetailsDto>> telegramSeasonNotifies = new HashMap<>();
        Map<User, Map<SeasonDto, TVDetailsDto>> emailSeasonNotifies = new HashMap<>();
        Map<User, Map<EpisodeDto, TVDetailsDto>> emailEpisodeNotifies = new HashMap<>();
        Map<User, Map<EpisodeDto, TVDetailsDto>> telegramEpisodeNotifies = new HashMap<>();

        // get all tv shows from db
        List<UserTVSeries> allUserTVSeries = tvSeriesService.getAllUserTVSeries();

        // @formatter:off
        // for each value from db
        allUserTVSeries.forEach(tv -> {

            // get foll tv show info
            Optional<TVDetailsDto> tvDetails =
                    tmdbTVSeriesService.getTVDetails(tv.getId().getTvSeriesId(),
                                                     tv.getUser().getLanguage());
            if (tvDetails.isPresent()) {
                TVDetailsDto tvDetailsDto = tvDetails.get();

                // for each season of tv show
                tvDetailsDto.getSeasons().forEach(s -> {

                    // get all season info
                    Optional<SeasonDto> seasonDetails =
                            tmdbTVSeriesService.getSeasonDetails(tvDetailsDto.getId(),
                                                                 s.getSeasonNumber(),
                                                                 tv.getUser().getLanguage());
                    if (seasonDetails.isPresent()) {
                        SeasonDto seasonDto = seasonDetails.get();


                        // if all episode dates matches season air date (season was released fully that date)
                        if (seasonDto.getEpisodes().stream().allMatch(e -> e.getAirDate().equals(seasonDto.getAirDate()))) {

                            // season air date matches condition
                            if (seasonDto.getAirDate().isBefore(endDate) && seasonDto.getAirDate().isAfter(startDate)) {

                                // notify about season
                                if (tv.getUser().isTelegramNotify()) {

                                    // add to telegram notify list
                                    addSeasonNotify(seasonDto, tvDetailsDto, tv.getUser(), telegramSeasonNotifies);
                                } else {

                                    // add to email notify list
                                    addSeasonNotify(seasonDto, tvDetailsDto, tv.getUser(), emailSeasonNotifies);
                                }
                            }

                        // season episodes DOES NOT matches season air date
                        } else {

                            // looking for episodes, the date of which matches condition
                            seasonDto.getEpisodes().stream().filter(e -> e.getAirDate().isBefore(endDate) && e.getAirDate().isAfter(startDate)).forEach(e -> {

                                // notify about episode
                                if(tv.getUser().isTelegramNotify()){

                                    // add to telegram notify list
                                    addEpisodeNotify(e, tvDetailsDto, tv.getUser(), telegramEpisodeNotifies);
                                } else {

                                    // add to email notify list
                                    addEpisodeNotify(e, tvDetailsDto, tv.getUser(), emailEpisodeNotifies);
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

        logger.info("Notified episodes by email: {}", emailEpisodeNotifies.toString());
        logger.info("Notified episodes by telegram: {}", telegramEpisodeNotifies.toString());
        logger.info("Notified seasons by email: {}", emailSeasonNotifies.toString());
        logger.info("Notified seasons by telegram: {}", telegramSeasonNotifies.toString());
    }


    private void addEpisodeNotify(EpisodeDto e, TVDetailsDto t, User user, Map<User, Map<EpisodeDto, TVDetailsDto>> notifies) {
        notifies.computeIfAbsent(user, k -> new HashMap<>());
        notifies.get(user).put(e, t);
    }

    private void addSeasonNotify(SeasonDto s, TVDetailsDto t, User user, Map<User, Map<SeasonDto, TVDetailsDto>> notifies) {
        notifies.computeIfAbsent(user, k -> new HashMap<>());
        notifies.get(user).put(s, t);
    }

    private void addMovieNotify(MovieDetailsDto movie, User user, ReleaseDate releaseDate, Map<User, Map<MovieDetailsDto, ReleaseDate>> notifies) {
        notifies.computeIfAbsent(user, k -> new HashMap<>());
        notifies.get(user).put(movie, releaseDate);
    }

    private void notifyMovieByEmail(Map<User, Map<MovieDetailsDto, ReleaseDate>> notifies) {
        notifies.forEach((user, value) -> value.forEach((movie, rd) -> emailService.sendMovieNotify(user, movie, rd)));
    }

    private void notifyMovieByTelegram(Map<User, Map<MovieDetailsDto, ReleaseDate>> notifies) {
        notifies.forEach((user, value) -> value.forEach((movie, rd) -> telegramService.sendMovieNotify(user, movie, rd)));
    }

    private void notifyEpisodeByTelegram(Map<User, Map<EpisodeDto, TVDetailsDto>> notifies) {
        notifies.forEach((user, value) -> value.forEach((episode, show) -> telegramService.sendEpisodeNotify(user, episode, show)));
    }

    private void notifyEpisodeByEmail(Map<User, Map<EpisodeDto, TVDetailsDto>> notifies) {
        notifies.forEach((user, value) -> value.forEach((episode, show) -> emailService.sendEpisodeNotify(user, episode, show)));
    }

    private void notifySeasonByTelegram(Map<User, Map<SeasonDto, TVDetailsDto>> notifies) {
        notifies.forEach((user, value) -> value.forEach((season, show) -> telegramService.sendSeasonNotify(user, season, show)));
    }

    private void notifySeasonByEmail(Map<User, Map<SeasonDto, TVDetailsDto>> notifies) {
        notifies.forEach((user, value) -> value.forEach((season, show) -> emailService.sendSeasonNotify(user, season, show)));
    }
}
