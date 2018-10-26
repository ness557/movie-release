package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.model.wrapper.tmdb.externalId.MovieResultList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import static java.lang.Thread.sleep;

@Service
public class ExternalIdServiceImpl implements ExternalIdService {

    private RestTemplate restTemplate = new RestTemplate();
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${tmdbapi.apikey}")
    private String apikey;

    @Value("${tmdbapi.url}")
    private String url;

    @Override
    public Integer getTmdbIdByImdbId(String imdbId) {
        UriComponentsBuilder movieBuilder = UriComponentsBuilder.fromHttpUrl(url + "find/")
                .path(imdbId)
                .queryParam("external_source", "imdb_id")
                .queryParam("api_key", apikey);
        ResponseEntity<MovieResultList> response = null;
        try {
            response = restTemplate.getForEntity(movieBuilder.toUriString(), MovieResultList.class);
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
                return this.getTmdbIdByImdbId(imdbId);
            }
            return 0;
        }

        Integer tmdbId = null;
        try {
            tmdbId = response.getBody().getMovieResultList().get(0).getId();
        } catch (Exception e) {
            logger.error("Could not get movie: {}", e.getMessage());
            return 0;
        }

        return tmdbId;
    }
}
