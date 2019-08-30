package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.dto.tmdb.externalId.TmdbMovieResultListDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import static java.lang.Thread.sleep;

@Service
@Slf4j
public class TmdbExternalIdServiceImpl implements TmdbExternalIdService {

    private RestTemplate restTemplate = new RestTemplate();

    @Value("${tmdbapi.apikey}")
    private String apikey;

    @Value("${tmdbapi.url}")
    private String url;

    @Override
    public Long getTmdbIdByImdbId(String imdbId) {
        UriComponentsBuilder movieBuilder = UriComponentsBuilder.fromHttpUrl(url + "find/")
                .path(imdbId)
                .queryParam("external_source", "imdb_id")
                .queryParam("api_key", apikey);
        ResponseEntity<TmdbMovieResultListDto> response = null;
        try {
            response = restTemplate.getForEntity(movieBuilder.toUriString(), TmdbMovieResultListDto.class);
        } catch (HttpStatusCodeException e) {
            log.error("Could not get movie: {}", e.getStatusCode().value());

            if (e.getStatusCode().value() == 429) {
                try {
                    // sleep current thread for 1s
                    sleep(1000);
                } catch (InterruptedException e1) {
                    log.error(e1.getMessage());
                }
                // and try again
                return this.getTmdbIdByImdbId(imdbId);
            }
            return 0L;
        }

        Long tmdbId = 0L;
        try {
            tmdbId = response.getBody().getMovieResultList().get(0).getId();
        } catch (Exception e) {
            log.error("Could not get movie: {}", e.getMessage());
        }

        return tmdbId;
    }
}
