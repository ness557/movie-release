package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.model.wrapper.tmdb.Language;
import com.ness.movie_release_web.model.wrapper.tmdb.movie.details.MovieDetails;
import com.ness.movie_release_web.model.wrapper.tmdb.movie.search.MovieSearch;
import com.ness.movie_release_web.model.wrapper.tmdb.releaseDates.ReleaseDate;
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
    public Optional<MovieDetails> getMovieDetails(Integer tmdbId, Language language) {

        UriComponentsBuilder movieUrlBuilder = UriComponentsBuilder.fromHttpUrl(url + "movie/")
                .path(tmdbId.toString())
                .queryParam("api_key", apikey)
                .queryParam("language", language.name());

        ResponseEntity<MovieDetails> response = null;
        try {
            response = restTemplate.getForEntity(movieUrlBuilder.toUriString(), MovieDetails.class);
        } catch (HttpStatusCodeException e) {
            logger.error("Could not get movie by id: {}, status: {}", tmdbId, e.getStatusCode().value());
            return Optional.empty();
        }

        MovieDetails result = response.getBody();

        if (result != null) {

            List<ReleaseDate> releaseDates = releaseDatesService.getReleaseDates(result.getId());
            result.setReleaseDates(releaseDates);
        }

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<MovieSearch> searchForMovies(String query, Integer page, Integer year, Language language) {
        UriComponentsBuilder searchUrlBuilder = UriComponentsBuilder.fromHttpUrl(url + "search/movie/")
                .queryParam("api_key", apikey)
                .queryParam("language", language.name())
                .queryParam("query", query)
                .queryParam("page", page == null ? 1 : page);

        if (year != null)
            searchUrlBuilder.queryParam("year", year);

        ResponseEntity<MovieSearch> response = null;
        try {
            response = restTemplate.getForEntity(searchUrlBuilder.build(false).toUriString(), MovieSearch.class);
        } catch (HttpStatusCodeException e) {
            logger.error("Could not search for movie: {}, status: ", query, e.getStatusCode().value());
            return Optional.empty();
        }
        return Optional.of(response.getBody());
    }
}
