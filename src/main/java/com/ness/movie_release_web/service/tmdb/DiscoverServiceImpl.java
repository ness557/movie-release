package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.model.wrapper.tmdb.Language;
import com.ness.movie_release_web.model.wrapper.tmdb.movie.discover.SortBy;
import com.ness.movie_release_web.model.wrapper.tmdb.movie.search.MovieSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

import static java.lang.Thread.sleep;

@Service
public class DiscoverServiceImpl implements DiscoverService {

    private RestTemplate restTemplate = new RestTemplate();
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${tmdbapi.apikey}")
    private String apikey;

    @Value("${tmdbapi.url}")
    private String url;

    @Override
    public Optional<MovieSearch> searchByGenre(Integer genreId, Integer page, SortBy sortBy, Language language) {

        UriComponentsBuilder movieBuilder = UriComponentsBuilder.fromHttpUrl(url + "discover/movie")
                .queryParam("api_key", apikey)
                .queryParam("sort_by", sortBy.getSearchString())
                .queryParam("language", language)
                .queryParam("page", page == null ? 1 : page)
                .queryParam("with_genres", genreId);

        ResponseEntity<MovieSearch> response = null;
        try {
            response = restTemplate.getForEntity(movieBuilder.toUriString(), MovieSearch.class);
        } catch (HttpStatusCodeException e) {
            logger.error("Could not get movie: {}", e.getStatusCode().value());

            if (e.getStatusCode().value() == 429) {
                try {
                    // sleep current thread for 1s
                    sleep(1000);
                } catch (InterruptedException e1) {
                    logger.error(e1.getMessage());
                }
                // and try again
                return this.searchByGenre(genreId, page, sortBy, language);
            }
            return Optional.empty();
        }

        return Optional.ofNullable(response.getBody());
    }
}
