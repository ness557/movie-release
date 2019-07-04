package com.ness.movie_release_web.service.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ness.movie_release_web.model.dto.tmdb.Language;
import com.ness.movie_release_web.model.dto.tmdb.GenreDto;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.lang.Thread.sleep;
import static java.util.Collections.emptyList;

@Service
public class GenreServiceImpl implements GenreService {

    private RestTemplate restTemplate = new RestTemplate();
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${tmdbapi.apikey}")
    private String apikey;

    @Value("${tmdbapi.url}")
    private String url;

    @Override
    public List<GenreDto> getMovieGenres(Language language) {

        UriComponentsBuilder movieBuilder = UriComponentsBuilder.fromHttpUrl(url + "genre/movie/list")
                .queryParam("api_key", apikey)
                .queryParam("language", language);

        ResponseEntity<GenreList> response;
        try {
            response = restTemplate.getForEntity(movieBuilder.toUriString(), GenreList.class);
        } catch (HttpStatusCodeException e) {
            logger.error("Could not get genres: {}", e.getStatusCode().value());

            if (e.getStatusCode().value() == 429) {
                try {
                    // sleep current thread for 1s
                    sleep(1000);
                } catch (InterruptedException e1) {
                    logger.error(e1.getMessage());
                }
                // and try again
                return this.getMovieGenres(language);
            }
            return emptyList();
        }

        return Optional.ofNullable(response.getBody()).map(GenreList::getGenres).orElse(Collections.emptyList());
    }

    @Override
    public List<GenreDto> getTVGenres(Language language) {
        UriComponentsBuilder movieBuilder = UriComponentsBuilder.fromHttpUrl(url + "genre/tv/list")
                .queryParam("api_key", apikey)
                .queryParam("language", language);

        ResponseEntity<GenreList> response;
        try {
            response = restTemplate.getForEntity(movieBuilder.toUriString(), GenreList.class);
        } catch (HttpStatusCodeException e) {
            logger.error("Could not get genres: {}", e.getStatusCode().value());

            if (e.getStatusCode().value() == 429) {
                try {
                    // sleep current thread for 1s
                    sleep(1000);
                } catch (InterruptedException e1) {
                    logger.error(e1.getMessage());
                }
                // and try again
                return this.getTVGenres(language);
            }
            return emptyList();
        }

        return Optional.ofNullable(response.getBody()).map(GenreList::getGenres).orElse(Collections.emptyList());
    }
}

@Getter
class GenreList{
    @JsonProperty("genres")
    private List<GenreDto> genres = new ArrayList<>();
}
