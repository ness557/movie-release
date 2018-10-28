package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.model.wrapper.tmdb.movie.discover.DiscoverSearchCriteria;
import com.ness.movie_release_web.model.wrapper.tmdb.movie.search.MovieSearch;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.format.DateTimeFormatter;
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
    public Optional<MovieSearch> searchByGenre(DiscoverSearchCriteria criteria) {

        UriComponentsBuilder movieBuilder = UriComponentsBuilder.fromHttpUrl(url + "discover/movie")
                .queryParam("api_key", apikey)
                .queryParam("sort_by", criteria.getSortBy().getSearchString())
                .queryParam("release_date.gte",
                        criteria.getReleaseDateMin().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .queryParam("release_date.lte",
                        criteria.getReleaseDateMax().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .queryParam("vote_average.gte", criteria.getVoteAverageMin())
                .queryParam("vote_average.lte", criteria.getVoteAverageMax())
                .queryParam("page", criteria.getPage())
                .queryParam("language", criteria.getLanguage());

        if (!criteria.getGenres().isEmpty())
            movieBuilder.queryParam("with_genres",
                    StringUtils.join(criteria.getGenres(), ","));

        if (!criteria.getCompanies().isEmpty())
            movieBuilder.queryParam("with_companies",
                    StringUtils.join(criteria.getCompanies(), ","));

        ResponseEntity<MovieSearch> response = null;
        try {
            response = restTemplate.getForEntity(movieBuilder.toUriString(), MovieSearch.class);
        } catch (HttpStatusCodeException e) {
            logger.error("Could not discover for movies: {}", e.getStatusCode().value());

            if (e.getStatusCode().value() == 429) {
                try {
                    // sleep current thread for 1s
                    sleep(1000);
                } catch (InterruptedException e1) {
                    logger.error(e1.getMessage());
                }
                // and try again
                return this.searchByGenre(criteria);
            }
            return Optional.empty();
        }

        return Optional.ofNullable(response.getBody());
    }
}
