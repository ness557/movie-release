package com.ness.movie_release_web.service.email;

import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.dto.tmdb.movie.details.TmdbMovieDetailsDto;
import com.ness.movie_release_web.dto.tmdb.releaseDates.TmdbReleaseDate;
import com.ness.movie_release_web.dto.tmdb.tvSeries.details.TmdbEpisodeDto;
import com.ness.movie_release_web.dto.tmdb.tvSeries.details.TmdbSeasonDto;
import com.ness.movie_release_web.dto.tmdb.tvSeries.details.TmdbTVDetailsDto;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.ui.freemarker.FreeMarkerTemplateUtils.processTemplateIntoString;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final Configuration freemarkerConfig;
    private final MessageSource messageSource;

    @Value("${telegram.webInterfaceLink}")
    private String webLink;

    @Override
    public void sendMovieNotify(User user, TmdbMovieDetailsDto movie, TmdbReleaseDate releaseDate) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            String localizedSubject = messageSource.getMessage("lang.email.subject.movie", new Object[]{}, user.getLanguage().getLocale());

            String subject = localizedSubject + " \"" +
                    movie.getTitle() +
                    " (" + movie.getReleaseDate().getYear() + ")" +
                    "\"";

            Map<String, String> model = new HashMap<String, String>() {{
                put("title", movie.getTitle());
                put("year", String.valueOf(movie.getReleaseDate().getYear()));
                put("poster", movie.getPosterPath());
                put("releaseDate", releaseDate.getReleaseDate()
                        .format(DateTimeFormatter.ofPattern("dd MMMM yyyy")
                                .withLocale(user.getLanguage().getLocale())));
                put("releaseType",
                        messageSource.getMessage("lang.release." + releaseDate.getReleaseType().name(), new Object[]{}, user.getLanguage().getLocale()));

                put("link", "http://" + webLink + "/movie/" + movie.getId());
            }};


            helper.setTo(user.getEmail());
            helper.setText(processTemplateIntoString(freemarkerConfig.getTemplate("emailMovieNotification.ftl", user.getLanguage().getLocale()), model), true);
            helper.setSubject(subject);

            mailSender.send(message);
            log.info("Email sent to {} with movie {}", user.getEmail(), movie.toString());
        } catch (IOException | MessagingException | TemplateException e) {
            log.error("Could not sent email: {}", e.getMessage());
        }
    }

    @Override
    public void sendEpisodeNotify(User user, TmdbEpisodeDto episode, TmdbTVDetailsDto show) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            String localizedSubject = messageSource.getMessage("lang.email.subject.episode", new Object[]{}, user.getLanguage().getLocale());

            String subject = localizedSubject + " \"" +
                    show.getName() +
                    " ( s" + episode.getSeasonNumber() + " e" + episode.getEpisodeNumber() + " )" +
                    "\"";

            Map<String, String> model = new HashMap<String, String>() {{
                put("show", show.getName());
                put("seasonNum", episode.getSeasonNumber().toString());
                put("episodeNum", episode.getEpisodeNumber().toString());
                put("poster", show.getPosterPath());
                put("releaseDate", episode.getAirDate()
                        .format(DateTimeFormatter.ofPattern("dd MMMM yyyy")
                                .withLocale(user.getLanguage().getLocale())));

                put("showLink", "http://" + webLink + "/series/" + show.getId());
                put("seasonLink", "http://" + webLink + "/series/" + show.getId() + "/season/" + episode.getSeasonNumber());
                put("episodeLink", "http://" + webLink + "/series/" + show.getId() + "/season/" + episode.getSeasonNumber() + "?episodeToOpen=" + episode.getEpisodeNumber());
            }};


            helper.setTo(user.getEmail());
            helper.setText(processTemplateIntoString(freemarkerConfig.getTemplate("emailEpisodeNotification.ftl", user.getLanguage().getLocale()), model), true);
            helper.setSubject(subject);

            mailSender.send(message);
            log.info("Email sent to {} with episode {} of tv show {}", user.getEmail(), episode.toString(), show.toString());
        } catch (Exception e) {
            log.error("Could not sent email: {}", e.getMessage());
        }
    }

    @Override
    public void sendSeasonNotify(User user, TmdbSeasonDto season, TmdbTVDetailsDto show) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            String localizedSubject = messageSource.getMessage("lang.email.subject.season", new Object[]{}, user.getLanguage().getLocale());

            String subject = localizedSubject + " \"" +
                    show.getName() +
                    " ( s" + season.getSeasonNumber() + " )" +
                    "\"";

            Map<String, String> model = new HashMap<String, String>() {{
                put("show", show.getName());
                put("seasonNum", season.getSeasonNumber().toString());
                put("poster", show.getPosterPath());
                put("releaseDate", season.getAirDate()
                        .format(DateTimeFormatter.ofPattern("dd MMMM yyyy")
                                .withLocale(user.getLanguage().getLocale())));

                put("showLink", "http://" + webLink + "/series/getSeries?tmdbId=" + show.getId());
                put("seasonLink", "http://" + webLink + "/series/" + show.getId() + "/season/" + season.getSeasonNumber());
            }};


            helper.setTo(user.getEmail());
            helper.setText(processTemplateIntoString(freemarkerConfig.getTemplate("emailSeasonNotification.ftl", user.getLanguage().getLocale()), model), true);
            helper.setSubject(subject);

            mailSender.send(message);
            log.info("Email sent to {} with season {} of tv show {}", user.getEmail(), season.toString(), show.toString());
        } catch (Exception e) {
            log.error("Could not sent email: {}", e.getMessage());
        }
    }

    @Override
    public void sendResetLink(String resetLink, String email) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            String subject = "Movie release password recovery";
            Map<String, String> params = Collections.singletonMap("resetLink", resetLink);

            helper.setSubject(subject);
            helper.setTo(email);
            helper.setText(processTemplateIntoString(freemarkerConfig.getTemplate("passwordRecovery.ftl"), params), true);

            mailSender.send(message);
        } catch (Exception e) {
            log.error("Could not sent email: {}", e.getMessage());
        }
    }
}

