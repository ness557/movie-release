package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.model.wrapper.tmdb.movie.discover.DiscoverSearchCriteria;
import com.ness.movie_release_web.model.wrapper.tmdb.movie.search.MovieSearchWrapper;
import com.ness.movie_release_web.model.wrapper.tmdb.tvSeries.search.TVSearchWrapper;
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
import static java.util.stream.Collectors.toList;

@Service
public class DiscoverServiceImpl implements DiscoverService {

    private RestTemplate restTemplate = new RestTemplate();
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${tmdbapi.apikey}")
    private String apikey;

    @Value("${tmdbapi.url}")
    private String url;

    @Override
    public Optional<MovieSearchWrapper> discoverMovie(DiscoverSearchCriteria criteria) {

        UriComponentsBuilder movieBuilder = UriComponentsBuilder.fromHttpUrl(url + "discover/movie")
                .queryParam("api_key", apikey)
                .queryParam("sort_by", criteria.getSortBy().getSearchString())
                .queryParam("vote_average.gte", criteria.getVoteAverageMin())
                .queryParam("vote_average.lte", criteria.getVoteAverageMax())
                .queryParam("page", criteria.getPage())
                .queryParam("language", criteria.getLanguage());

        if (criteria.getReleaseDateMin() != null)
            movieBuilder.queryParam("release_date.gte",
                    criteria.getReleaseDateMin().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        if (criteria.getReleaseDateMax() != null)
            movieBuilder.queryParam("release_date.lte",
                    criteria.getReleaseDateMax().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        if (!criteria.getGenres().isEmpty())
            movieBuilder.queryParam("with_genres",
                    StringUtils.join(criteria.getGenres(), criteria.getGenresAnd() ? "," : "|"));

        if (!criteria.getReleaseTypes().isEmpty())
            movieBuilder.queryParam("with_release_type",
                    StringUtils.join(criteria.getReleaseTypes().stream().map(Enum::ordinal).collect(toList()), criteria.getReleaseTypeAnd() ? "," : "|"));

        if (!criteria.getCompanies().isEmpty())
            movieBuilder.queryParam("with_companies",
                    StringUtils.join(criteria.getCompanies(), criteria.getCompaniesAnd() ? "," : "|"));

        if (!criteria.getPeople().isEmpty())
            movieBuilder.queryParam("with_people",
                    StringUtils.join(criteria.getPeople(), criteria.getPeopleAnd() ? "," : "|"));

        ResponseEntity<MovieSearchWrapper> response = null;
        try {
            response = restTemplate.getForEntity(movieBuilder.build(false).toUriString(), MovieSearchWrapper.class);
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
                return this.discoverMovie(criteria);
            }
            return Optional.empty();
        }

        return Optional.ofNullable(response.getBody());
    }

    @Override
    public Optional<TVSearchWrapper> discoverSeries(DiscoverSearchCriteria criteria) {
        UriComponentsBuilder movieBuilder = UriComponentsBuilder.fromHttpUrl(url + "discover/tv")
                .queryParam("api_key", apikey)
                .queryParam("sort_by", criteria.getSortBy().getSearchString())
                .queryParam("vote_average.gte", criteria.getVoteAverageMin())
                .queryParam("vote_average.lte", criteria.getVoteAverageMax())
                .queryParam("page", criteria.getPage())
                .queryParam("language", criteria.getLanguage());

        if (criteria.getReleaseDateMin() != null)
            movieBuilder.queryParam("first_air_date.gte",
                    criteria.getReleaseDateMin().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        if (criteria.getReleaseDateMax() != null)
            movieBuilder.queryParam("first_air_date.lte",
                    criteria.getReleaseDateMax().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        if (!criteria.getGenres().isEmpty())
            movieBuilder.queryParam("with_genres",
                    StringUtils.join(criteria.getGenres(), criteria.getGenresAnd() ? "," : "|"));

        if (!criteria.getNetworks().isEmpty())
            movieBuilder.queryParam("with_networks",
                    StringUtils.join(criteria.getNetworks(), criteria.getNetworksAnd() ? "," : "|"));

        if (!criteria.getCompanies().isEmpty())
            movieBuilder.queryParam("with_companies",
                    StringUtils.join(criteria.getCompanies(), criteria.getCompaniesAnd() ? "," : "|"));

        ResponseEntity<TVSearchWrapper> response;
        try {
            response = restTemplate.getForEntity(movieBuilder.build(false).toUriString(), TVSearchWrapper.class);
        } catch (HttpStatusCodeException e) {
            logger.error("Could not discover for series: {}", e.getStatusCode().value());

            if (e.getStatusCode().value() == 429) {
                try {
                    // sleep current thread for 1s
                    sleep(1000);
                } catch (InterruptedException e1) {
                    logger.error(e1.getMessage());
                }
                // and try again
                return this.discoverSeries(criteria);
            }
            return Optional.empty();
        }

        return Optional.ofNullable(response.getBody());
    }
}
