package com.ness.movie_release_web.service.telegram;

import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.dto.tmdb.movie.details.TmdbMovieDetailsDto;
import com.ness.movie_release_web.dto.tmdb.releaseDates.TmdbReleaseDate;
import com.ness.movie_release_web.dto.tmdb.tvSeries.details.TmdbEpisodeDto;
import com.ness.movie_release_web.dto.tmdb.tvSeries.details.TmdbSeasonDto;
import com.ness.movie_release_web.dto.tmdb.tvSeries.details.TmdbTVDetailsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@Slf4j
@RequiredArgsConstructor
public class TelegramServiceImpl implements TelegramService {

    private final TelegramNotificationBot notificationBot;
    private final MessageSource messageSource;

    @Value("${telegram.name}")
    private String botName;

    @Value("${telegram.token}")
    private String token;

    @Value("${telegram.webInterfaceLink}")
    private String appLink;


    @Override
    public void sendMovieNotify(User user, TmdbMovieDetailsDto movie, TmdbReleaseDate releaseDate) {

        String resultText = new StringBuilder()
                .append(movie.getTitle())
                .append(" (")
                .append(movie.getReleaseDate().getYear())
                .append(") ")
                .append(messageSource.getMessage("lang.telegram.had_a", new Object[]{}, user.getLanguage().getLocale()))
                .append(" *")
                .append(messageSource.getMessage("lang.release." + releaseDate.getReleaseType().name(), new Object[]{}, user.getLanguage().getLocale()))
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
    public void sendEpisodeNotify(User user, TmdbEpisodeDto episode, TmdbTVDetailsDto show) {

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
    public void sendSeasonNotify(User user, TmdbSeasonDto season, TmdbTVDetailsDto show) {

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
