package com.ness.movie_release_web.service.email;

import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.model.wrapper.tmdb.movie.details.MovieDetailsWrapper;
import com.ness.movie_release_web.model.wrapper.tmdb.releaseDates.ReleaseDate;
import com.ness.movie_release_web.model.wrapper.tmdb.tvSeries.details.EpisodeWrapper;
import com.ness.movie_release_web.model.wrapper.tmdb.tvSeries.details.SeasonWrapper;
import com.ness.movie_release_web.model.wrapper.tmdb.tvSeries.details.TVDetailsWrapper;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.HashMap;
import java.util.Map;

import static org.springframework.ui.freemarker.FreeMarkerTemplateUtils.processTemplateIntoString;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private Configuration freemarkerConfig;

    @Autowired
    private MessageSource messageSource;

    @Value("${telegram.webInterfaceLink}")
    private String webLink;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void sendMovieNotify(User user, MovieDetailsWrapper movie, ReleaseDate releaseDate) {

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
            logger.info("Email sent to {} with movie {}", user.getEmail(), movie.toString());
        } catch (IOException | MessagingException | TemplateException e) {
            logger.error("Could not sent email: {}", e.getMessage());
        }
    }

    @Override
    public void sendEpisodeNotify(User user, EpisodeWrapper episode, TVDetailsWrapper show) {

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
            logger.info("Email sent to {} with episode {} of tv show {}", user.getEmail(), episode.toString(), show.toString());
        } catch (IOException | MessagingException | TemplateException e) {
            logger.error("Could not sent email: {}", e.getMessage());
        }
    }

    @Override
    public void sendSeasonNotify(User user, SeasonWrapper season, TVDetailsWrapper show) {
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
            logger.info("Email sent to {} with season {} of tv show {}", user.getEmail(), season.toString(), show.toString());
        } catch (IOException | MessagingException | TemplateException e) {
            logger.error("Could not sent email: {}", e.getMessage());
        }
    }
}
