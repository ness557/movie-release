package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.dto.Language;
import com.ness.movie_release_web.dto.tmdb.movie.details.TmdbMovieDetailsDto;
import com.ness.movie_release_web.dto.tmdb.movie.search.TmdbMovieSearchDto;
import com.ness.movie_release_web.dto.tmdb.releaseDates.TmdbReleaseDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

import static java.lang.Thread.sleep;

@Service
@RequiredArgsConstructor
@Slf4j
public class TmdbMovieServiceImpl implements TmdbMovieService {

    private RestTemplate restTemplate = new RestTemplate();
    private final TmdbDatesService releaseDatesService;

    @Value("${tmdbapi.apikey}")
    private String apikey;

    @Value("${tmdbapi.url}")
    private String url;

    @Override
    @Cacheable("getMovieDetails")
    public Optional<TmdbMovieDetailsDto> getMovieDetails(Long tmdbId, Language language) {

        UriComponentsBuilder movieUrlBuilder = UriComponentsBuilder.fromHttpUrl(url + "movie/")
                .path(tmdbId.toString())
                .queryParam("api_key", apikey)
                .queryParam("append_to_response", "credits,videos")
                .queryParam("language", language.name());

        ResponseEntity<TmdbMovieDetailsDto> response;
        try {
            response = restTemplate.getForEntity(movieUrlBuilder.toUriString(), TmdbMovieDetailsDto.class);
        } catch (HttpStatusCodeException e) {
            log.error("Could not get movie by id: {}, status: {}", tmdbId, e.getStatusCode().value());

            // if there are too many requests
            if (e.getStatusCode().value() == 429) {
                try {
                    // sleep current thread for 1s
                    sleep(1000);
                } catch (InterruptedException e1) {
                    log.error(e1.getMessage());
                }
                // and try again
                return this.getMovieDetails(tmdbId, language);
            }
            return Optional.empty();
        }

        TmdbMovieDetailsDto result = response.getBody();

        if (result != null) {

            List<TmdbReleaseDate> releaseDates = releaseDatesService.getReleaseDates(result.getId());
            result.setReleaseDates(releaseDates);
        }

        return Optional.ofNullable(result);
    }

    @Override
    @Cacheable("searchForMovies")
    public Optional<TmdbMovieSearchDto> searchForMovies(String query, Long page, Long year, Language language) {
        UriComponentsBuilder searchUrlBuilder = UriComponentsBuilder.fromHttpUrl(url + "search/movie/")
                .queryParam("api_key", apikey)
                .queryParam("language", language.name())
                .queryParam("query", query)
                .queryParam("page", page == null ? 1 : page);

        if (year != null)
            searchUrlBuilder.queryParam("year", year);

        ResponseEntity<TmdbMovieSearchDto> response;
        try {
            response = restTemplate.getForEntity(searchUrlBuilder.build(false).toUriString(), TmdbMovieSearchDto.class);
        } catch (HttpStatusCodeException e) {
            log.error("Could not search for movie: {}, status: {}", query, e.getStatusCode().value());

            if (e.getStatusCode().value() == 429) {
                try {
                    // sleep current thread for 1s
                    sleep(1000);
                } catch (InterruptedException e1) {
                    log.error(e1.getMessage());
                }
                // and try again
                return this.searchForMovies(query, page, year, language);
            }

            return Optional.empty();
        }
        return Optional.ofNullable(response.getBody());
    }
}
