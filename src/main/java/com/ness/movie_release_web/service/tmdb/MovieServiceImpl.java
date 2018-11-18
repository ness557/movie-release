package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.model.wrapper.tmdb.Language;
import com.ness.movie_release_web.model.wrapper.tmdb.movie.details.MovieDetailsWrapper;
import com.ness.movie_release_web.model.wrapper.tmdb.movie.search.MovieSearchWrapper;
import com.ness.movie_release_web.model.wrapper.tmdb.releaseDates.ReleaseDateWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

import static java.lang.Thread.sleep;

@Service
public class MovieServiceImpl implements MovieService {

    private RestTemplate restTemplate = new RestTemplate();
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${tmdbapi.apikey}")
    private String apikey;

    @Value("${tmdbapi.url}")
    private String url;

    @Autowired
    private TmdbDatesService releaseDatesService;

    @Override
    public Optional<MovieDetailsWrapper> getMovieDetails(Integer tmdbId, Language language) {

        UriComponentsBuilder movieUrlBuilder = UriComponentsBuilder.fromHttpUrl(url + "movie/")
                .path(tmdbId.toString())
                .queryParam("api_key", apikey)
                .queryParam("append_to_response", "credits,videos")
                .queryParam("language", language.name());

        ResponseEntity<MovieDetailsWrapper> response = null;
        try {
            response = restTemplate.getForEntity(movieUrlBuilder.toUriString(), MovieDetailsWrapper.class);
        } catch (HttpStatusCodeException e) {
            logger.error("Could not get movie by id: {}, status: {}", tmdbId, e.getStatusCode().value());

            // if there are too many requests
            if (e.getStatusCode().value() == 429) {
                try {
                    // sleep current thread for 1s
                    sleep(1000);
                } catch (InterruptedException e1) {
                    logger.error(e1.getMessage());
                }
                // and try again
                return this.getMovieDetails(tmdbId, language);
            }
            return Optional.empty();
        }

        MovieDetailsWrapper result = response.getBody();

        if (result != null) {

            List<ReleaseDateWrapper> releaseDateWrappers = releaseDatesService.getReleaseDates(result.getId());
            result.setReleaseDateWrappers(releaseDateWrappers);
        }

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<MovieSearchWrapper> searchForMovies(String query, Integer page, Integer year, Language language) {
        UriComponentsBuilder searchUrlBuilder = UriComponentsBuilder.fromHttpUrl(url + "search/movie/")
                .queryParam("api_key", apikey)
                .queryParam("language", language.name())
                .queryParam("query", query)
                .queryParam("page", page == null ? 1 : page);

        if (year != null)
            searchUrlBuilder.queryParam("year", year);

        ResponseEntity<MovieSearchWrapper> response = null;
        try {
            response = restTemplate.getForEntity(searchUrlBuilder.build(false).toUriString(), MovieSearchWrapper.class);
        } catch (HttpStatusCodeException e) {
            logger.error("Could not search for movie: {}, status: ", query, e.getStatusCode().value());

            if (e.getStatusCode().value() == 429) {
                try {
                    // sleep current thread for 1s
                    sleep(1000);
                } catch (InterruptedException e1) {
                    logger.error(e1.getMessage());
                }
                // and try again
                return this.searchForMovies(query, page, year, language);
            }

            return Optional.empty();
        }
        return Optional.ofNullable(response.getBody());
    }
}
