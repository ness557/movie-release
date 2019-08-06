package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.model.dto.tmdb.Language;
import com.ness.movie_release_web.model.dto.tmdb.movie.details.MovieDetailsDto;
import com.ness.movie_release_web.model.dto.tmdb.movie.search.MovieSearchDto;
import com.ness.movie_release_web.model.dto.tmdb.releaseDates.ReleaseDate;
import com.ness.movie_release_web.service.tmdb.cache.CacheProvider;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class TmdbMovieServiceImpl implements TmdbMovieService {

    private RestTemplate restTemplate = new RestTemplate();
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CacheProvider<MovieDetailsDto> cacheProvider;
    private final TmdbDatesService releaseDatesService;

    @Value("${tmdbapi.apikey}")
    private String apikey;

    @Value("${tmdbapi.url}")
    private String url;

    @Override
    public Optional<MovieDetailsDto> getMovieDetails(Long tmdbId, Language language) {

        Optional<MovieDetailsDto> fromCache = cacheProvider.getFromCache(tmdbId, language);
        if (fromCache.isPresent()) {
            return fromCache;
        }

        UriComponentsBuilder movieUrlBuilder = UriComponentsBuilder.fromHttpUrl(url + "movie/")
                .path(tmdbId.toString())
                .queryParam("api_key", apikey)
                .queryParam("append_to_response", "credits,videos")
                .queryParam("language", language.name());

        ResponseEntity<MovieDetailsDto> response;
        try {
            response = restTemplate.getForEntity(movieUrlBuilder.toUriString(), MovieDetailsDto.class);
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

        MovieDetailsDto result = response.getBody();

        if (result != null) {

            List<ReleaseDate> releaseDates = releaseDatesService.getReleaseDates(result.getId());
            result.setReleaseDates(releaseDates);
        }

        cacheProvider.putToCache(tmdbId, response.getBody(), language);

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<MovieSearchDto> searchForMovies(String query, Integer page, Long year, Language language) {
        UriComponentsBuilder searchUrlBuilder = UriComponentsBuilder.fromHttpUrl(url + "search/movie/")
                .queryParam("api_key", apikey)
                .queryParam("language", language.name())
                .queryParam("query", query)
                .queryParam("page", page == null ? 1 : page);

        if (year != null)
            searchUrlBuilder.queryParam("year", year);

        ResponseEntity<MovieSearchDto> response;
        try {
            response = restTemplate.getForEntity(searchUrlBuilder.build(false).toUriString(), MovieSearchDto.class);
        } catch (HttpStatusCodeException e) {
            logger.error("Could not search for movie: {}, status: {}", query, e.getStatusCode().value());

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
