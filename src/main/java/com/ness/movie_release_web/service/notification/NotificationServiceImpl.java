package com.ness.movie_release_web.service.notification;

import com.ness.movie_release_web.model.Film;
import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.model.UserTVSeries;
import com.ness.movie_release_web.model.wrapper.tmdb.Language;
import com.ness.movie_release_web.model.wrapper.tmdb.movie.details.MovieDetailsWrapper;
import com.ness.movie_release_web.model.wrapper.tmdb.releaseDates.ReleaseDate;
import com.ness.movie_release_web.model.wrapper.tmdb.releaseDates.ReleaseType;
import com.ness.movie_release_web.model.wrapper.tmdb.tvSeries.details.EpisodeWrapper;
import com.ness.movie_release_web.model.wrapper.tmdb.tvSeries.details.SeasonWrapper;
import com.ness.movie_release_web.model.wrapper.tmdb.tvSeries.details.TVDetailsWrapper;
import com.ness.movie_release_web.service.FilmService;
import com.ness.movie_release_web.service.TVSeriesService;
import com.ness.movie_release_web.service.email.EmailService;
import com.ness.movie_release_web.service.telegram.TelegramService;
import com.ness.movie_release_web.service.tmdb.MovieService;
import com.ness.movie_release_web.service.tmdb.TmdbDatesService;
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
    private MovieService movieService;
    private TelegramService telegramService;
    private EmailService emailService;
    private TmdbDatesService tmdbDatesService;
    private TVSeriesService tvSeriesService;
    private TmdbTVSeriesService tmdbTVSeriesService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public NotificationServiceImpl(FilmService filmService,
                                   MovieService movieService,
                                   TelegramService telegramService,
                                   EmailService emailService,
                                   TmdbDatesService tmdbDatesService,
                                   TVSeriesService tvSeriesService,
                                   TmdbTVSeriesService tmdbTVSeriesService) {
        this.filmService = filmService;
        this.telegramService = telegramService;
        this.emailService = emailService;
        this.movieService = movieService;
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

        Map<User, Map<MovieDetailsWrapper, ReleaseDate>> telegramNotifies = new HashMap<>();
        Map<User, Map<MovieDetailsWrapper, ReleaseDate>> emailNotifies = new HashMap<>();

        // notifies about release
        films.forEach((f) -> {

            List<ReleaseDate> releaseDates =
                    tmdbDatesService.getReleaseDates(
                            f.getId().intValue(),
                            ReleaseType.Theatrical,
                            ReleaseType.Digital,
                            ReleaseType.Physical);

            Map<Language, MovieDetailsWrapper> langMovie =
                    Arrays.stream(Language.values()).collect(toMap(l -> l, l -> movieService.getMovieDetails(f.getId().intValue(), l).orElse(new MovieDetailsWrapper())));

            f.getUsers().forEach(u -> {
                releaseDates.forEach(rd -> {
                    if (rd.getReleaseDate().isBefore(endDate) && rd.getReleaseDate().isAfter(startDate)) {
                        if (u.isTelegramNotify()) {
                            addMovieNotify(langMovie.get(u.getLanguage()), u, rd, telegramNotifies);
                        } else {
                            addMovieNotify(langMovie.get(u.getLanguage()), u, rd, emailNotifies);
                        }
                    }
                });

            });
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


        Map<User, Map<SeasonWrapper, TVDetailsWrapper>> telegramSeasonNotifies = new HashMap<>();
        Map<User, Map<SeasonWrapper, TVDetailsWrapper>> emailSeasonNotifies = new HashMap<>();
        Map<User, Map<EpisodeWrapper, TVDetailsWrapper>> emailEpisodeNotifies = new HashMap<>();
        Map<User, Map<EpisodeWrapper, TVDetailsWrapper>> telegramEpisodeNotifies = new HashMap<>();

        // get all tv shows from db
        List<UserTVSeries> allUserTVSeries = tvSeriesService.getAllUserTVSeries();

        // @formatter:off
        // for each value from db
        allUserTVSeries.forEach(tv -> {

            // get foll tv show info
            Optional<TVDetailsWrapper> tvDetails =
                    tmdbTVSeriesService.getTVDetails(tv.getId().getTvSeriesId().intValue(),
                                                     tv.getUser().getLanguage());
            if (tvDetails.isPresent()) {
                TVDetailsWrapper tvDetailsWrapper = tvDetails.get();

                // for each season of tv show
                tvDetailsWrapper.getSeasons().forEach(s -> {

                    // get all season info
                    Optional<SeasonWrapper> seasonDetails =
                            tmdbTVSeriesService.getSeasonDetails(tvDetailsWrapper.getId(),
                                                                 s.getSeasonNumber(),
                                                                 tv.getUser().getLanguage());
                    if (seasonDetails.isPresent()) {
                        SeasonWrapper seasonWrapper = seasonDetails.get();


                        // if all episode dates matches season air date (season was released fully that date)
                        if (seasonWrapper.getEpisodes().stream().allMatch(e -> e.getAirDate().equals(seasonWrapper.getAirDate()))) {

                            // season air date matches condition
                            if (seasonWrapper.getAirDate().isBefore(endDate) && seasonWrapper.getAirDate().isAfter(startDate)) {

                                // notify about season
                                if (tv.getUser().isTelegramNotify()) {

                                    // add to telegram notify list
                                    addSeasonNotify(seasonWrapper, tvDetailsWrapper, tv.getUser(), telegramSeasonNotifies);
                                } else {

                                    // add to email notify list
                                    addSeasonNotify(seasonWrapper, tvDetailsWrapper, tv.getUser(), emailSeasonNotifies);
                                }
                            }

                        // season episodes DOES NOT matches season air date
                        } else {

                            // looking for episodes, the date of which matches condition
                            seasonWrapper.getEpisodes().stream().filter(e -> e.getAirDate().isBefore(endDate) && e.getAirDate().isAfter(startDate)).forEach(e -> {

                                // notify about episode
                                if(tv.getUser().isTelegramNotify()){

                                    // add to telegram notify list
                                    addEpisodeNotify(e, tvDetailsWrapper, tv.getUser(), telegramEpisodeNotifies);
                                } else {

                                    // add to email notify list
                                    addEpisodeNotify(e, tvDetailsWrapper, tv.getUser(), emailEpisodeNotifies);
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


    private void addEpisodeNotify(EpisodeWrapper e, TVDetailsWrapper t, User user, Map<User, Map<EpisodeWrapper, TVDetailsWrapper>> notifies) {
        notifies.computeIfAbsent(user, k -> new HashMap<>());
        notifies.get(user).put(e, t);
    }

    private void addSeasonNotify(SeasonWrapper s, TVDetailsWrapper t, User user, Map<User, Map<SeasonWrapper, TVDetailsWrapper>> notifies) {
        notifies.computeIfAbsent(user, k -> new HashMap<>());
        notifies.get(user).put(s, t);
    }

    private void addMovieNotify(MovieDetailsWrapper movie, User user, ReleaseDate releaseDate, Map<User, Map<MovieDetailsWrapper, ReleaseDate>> notifies) {
        notifies.computeIfAbsent(user, k -> new HashMap<>());
        notifies.get(user).put(movie, releaseDate);
    }

    private void notifyMovieByEmail(Map<User, Map<MovieDetailsWrapper, ReleaseDate>> notifies) {
        notifies.forEach((user, value) -> value.forEach((movie, rd) -> emailService.sendMovieNotify(user, movie, rd)));
    }

    private void notifyMovieByTelegram(Map<User, Map<MovieDetailsWrapper, ReleaseDate>> notifies) {
        notifies.forEach((user, value) -> value.forEach((movie, rd) -> telegramService.sendMovieNotify(user, movie, rd)));
    }

    private void notifyEpisodeByTelegram(Map<User, Map<EpisodeWrapper, TVDetailsWrapper>> notifies) {
        notifies.forEach((user, value) -> value.forEach((episode, show) -> telegramService.sendEpisodeNotify(user, episode, show)));
    }

    private void notifyEpisodeByEmail(Map<User, Map<EpisodeWrapper, TVDetailsWrapper>> notifies) {
        notifies.forEach((user, value) -> value.forEach((episode, show)-> emailService.sendEpisodeNotify(user, episode, show)));
    }

    private void notifySeasonByTelegram(Map<User, Map<SeasonWrapper, TVDetailsWrapper>> notifies) {
        notifies.forEach((user, value) -> value.forEach((season, show)-> telegramService.sendSeasonNotify(user, season, show)));
    }

    private void notifySeasonByEmail(Map<User, Map<SeasonWrapper, TVDetailsWrapper>> notifies) {
        notifies.forEach((user, value) -> value.forEach((season, show)-> emailService.sendSeasonNotify(user, season, show)));
    }
}
