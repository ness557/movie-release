package com.ness.movie_release_web.service.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ness.movie_release_web.dto.tmdb.TmdbGenreDto;
import com.ness.movie_release_web.dto.Language;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class TmdbGenreServiceImpl implements TmdbGenreService {

    private RestTemplate restTemplate = new RestTemplate();

    @Value("${tmdbapi.apikey}")
    private String apikey;

    @Value("${tmdbapi.url}")
    private String url;

    @Override
    public List<TmdbGenreDto> getMovieGenres(Language language) {

        UriComponentsBuilder movieBuilder = UriComponentsBuilder.fromHttpUrl(url + "genre/movie/list")
                .queryParam("api_key", apikey)
                .queryParam("language", language);

        ResponseEntity<GenreList> response;
        try {
            response = restTemplate.getForEntity(movieBuilder.toUriString(), GenreList.class);
        } catch (HttpStatusCodeException e) {
            log.error("Could not get genres: {}", e.getStatusCode().value());

            if (e.getStatusCode().value() == 429) {
                try {
                    // sleep current thread for 1s
                    sleep(1000);
                } catch (InterruptedException e1) {
                    log.error(e1.getMessage());
                }
                // and try again
                return this.getMovieGenres(language);
            }
            return emptyList();
        }

        return Optional.ofNullable(response.getBody()).map(GenreList::getGenres).orElse(Collections.emptyList());
    }

    @Override
    public List<TmdbGenreDto> getTVGenres(Language language) {
        UriComponentsBuilder movieBuilder = UriComponentsBuilder.fromHttpUrl(url + "genre/tv/list")
                .queryParam("api_key", apikey)
                .queryParam("language", language);

        ResponseEntity<GenreList> response;
        try {
            response = restTemplate.getForEntity(movieBuilder.toUriString(), GenreList.class);
        } catch (HttpStatusCodeException e) {
            log.error("Could not get genres: {}", e.getStatusCode().value());

            if (e.getStatusCode().value() == 429) {
                try {
                    // sleep current thread for 1s
                    sleep(1000);
                } catch (InterruptedException e1) {
                    log.error(e1.getMessage());
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
    private List<TmdbGenreDto> genres = new ArrayList<>();
}
