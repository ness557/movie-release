package com.ness.movie_release_web.service.telegram;

import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.model.wrapper.tmdb.movie.details.MovieDetailsWrapper;
import com.ness.movie_release_web.model.wrapper.tmdb.releaseDates.ReleaseDate;
import com.ness.movie_release_web.model.wrapper.tmdb.tvSeries.details.EpisodeWrapper;
import com.ness.movie_release_web.model.wrapper.tmdb.tvSeries.details.SeasonWrapper;
import com.ness.movie_release_web.model.wrapper.tmdb.tvSeries.details.TVDetailsWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;

import java.time.format.DateTimeFormatter;

@Service
public class TelegramServiceImpl implements TelegramService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private TelegramNotificationBot notificationBot;

    @Value("${telegram.name}")
    private String botName;

    @Value("${telegram.token}")
    private String token;

    @Value("${telegram.webInterfaceLink}")
    private String appLink;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    public TelegramServiceImpl(TelegramNotificationBot bot) {

        notificationBot = bot;

        // instantiate Tg bot api
        TelegramBotsApi botsApi = new TelegramBotsApi();

        try {
            botsApi.registerBot((LongPollingBot) notificationBot);
        } catch (TelegramApiRequestException e) {
            logger.info("Couldn't initialize bot: {}", e.getMessage());
        }
    }

    @Override
    public void sendMovieNotify(User user, MovieDetailsWrapper movie, ReleaseDate releaseDate) {

        String resultText = new StringBuilder()
                .append(movie.getTitle())
                .append(" (")
                .append(movie.getReleaseDate().getYear())
                .append(") ")
                .append(messageSource.getMessage("lang.telegram.had_a", new Object[]{}, user.getLanguage().getLocale()))
                .append(" *")
                .append(messageSource.getMessage("lang.release_type." + releaseDate.getReleaseType().name(), new Object[]{}, user.getLanguage().getLocale()))
                .append("* ")
                .append(messageSource.getMessage("lang.telegram.released_at", new Object[]{}, user.getLanguage().getLocale()))
                .append(" ")
                .append(releaseDate.getReleaseDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")
                        .withLocale(user.getLanguage().getLocale())))

                .append("\n[")
                .append(messageSource.getMessage("lang.telegram.more_info", new Object[]{}, user.getLanguage().getLocale()))
                .append("](http://")
                .append(appLink)
                .append("/movie/")
                .append(movie.getId())
                .append(")")
                .toString();

        notificationBot.sendNotify(resultText, user.getTelegramChatId(), movie.getPosterPath());
    }

    @Override
    public void sendEpisodeNotify(User user, EpisodeWrapper episode, TVDetailsWrapper show) {

        String resultText = new StringBuilder()
                .append(episode.getAirDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy").withLocale(user.getLanguage().getLocale()))).append(" ")
                .append(messageSource.getMessage("lang.telegram.series.released", new Object[]{}, user.getLanguage().getLocale())).append(" ")
                .append(episode.getEpisodeNumber()).append(" ")
                .append(messageSource.getMessage("lang.telegram.series.episode", new Object[]{}, user.getLanguage().getLocale())).append(" ")
                .append(episode.getSeasonNumber()).append(" ")
                .append(messageSource.getMessage("lang.telegram.series.season.ru", new Object[]{}, user.getLanguage().getLocale())).append(" ")
                .append(show.getName()).append(" ")

                .append("\n[")
                .append(messageSource.getMessage("lang.telegram.more_info", new Object[]{}, user.getLanguage().getLocale()))
                .append("](http://")
                .append(appLink)
                .append("/series/")
                .append(show.getId())
                .append("/season/")
                .append(episode.getSeasonNumber())
                .append("?episodeToOpen=")
                .append(episode.getEpisodeNumber())
                .append(")")
                .toString();

        notificationBot.sendNotify(resultText, user.getTelegramChatId(), show.getPosterPath());
    }

    @Override
    public void sendSeasonNotify(User user, SeasonWrapper season, TVDetailsWrapper show) {

        String resultText = new StringBuilder()
                .append(season.getAirDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy").withLocale(user.getLanguage().getLocale()))).append(" ")
                .append(messageSource.getMessage("lang.telegram.series.released", new Object[]{}, user.getLanguage().getLocale())).append(" ")
                .append(season.getSeasonNumber()).append(" ")
                .append(messageSource.getMessage("lang.telegram.series.season", new Object[]{}, user.getLanguage().getLocale())).append(" ")
                .append(show.getName()).append(" ")

                .append("\n[")
                .append(messageSource.getMessage("lang.telegram.more_info", new Object[]{}, user.getLanguage().getLocale()))
                .append("](http://")
                .append(appLink)
                .append("/series/")
                .append(show.getId())
                .append("/season/")
                .append(season.getSeasonNumber())
                .append(")")
                .toString();

        Long telegramChatId = user.getTelegramChatId();
        String posterPath = show.getPosterPath();

        notificationBot.sendNotify(resultText, telegramChatId, posterPath);
    }
}
