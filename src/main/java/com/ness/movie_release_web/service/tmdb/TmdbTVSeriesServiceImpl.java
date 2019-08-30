package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.dto.Language;
import com.ness.movie_release_web.dto.tmdb.tvSeries.details.TmdbEpisodeDto;
import com.ness.movie_release_web.dto.tmdb.tvSeries.details.TmdbSeasonDto;
import com.ness.movie_release_web.dto.tmdb.tvSeries.details.TmdbTVDetailsDto;
import com.ness.movie_release_web.dto.tmdb.tvSeries.search.TmdbTVSearchDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

import static java.lang.Thread.sleep;

@Service
@Slf4j
@RequiredArgsConstructor
public class TmdbTVSeriesServiceImpl implements TmdbTVSeriesService {

    private RestTemplate restTemplate = new RestTemplate();

    @Value("${tmdbapi.apikey}")
    private String apikey;

    @Value("${tmdbapi.url}")
    private String url;

    @Override
    @Cacheable("getTVDetails")
    public Optional<TmdbTVDetailsDto> getTVDetails(Long tmdbId, Language language) {

        UriComponentsBuilder UrlBuilder = UriComponentsBuilder.fromHttpUrl(url + "tv/")
                .path(tmdbId.toString())
                .queryParam("api_key", apikey)
                .queryParam("language", language.name())
                .queryParam("append_to_response", "next_episode_to_air,credits,videos");

        ResponseEntity<TmdbTVDetailsDto> response;
        try {
            response = restTemplate.getForEntity(UrlBuilder.toUriString(), TmdbTVDetailsDto.class);
        } catch (HttpStatusCodeException e) {
            log.error("Could not get tv details by id: {}, status: {}", tmdbId, e.getStatusCode().value());

            // if there are too many requests
            if (e.getStatusCode().value() == 429) {
                try {
                    // sleep current thread for 1s
                    sleep(1000);
                } catch (InterruptedException e1) {
                    log.error(e1.getMessage());
                }
                // and try again
                return this.getTVDetails(tmdbId, language);
            }
            return Optional.empty();
        }

        return Optional.ofNullable(response.getBody());
    }

    @Override
    @Cacheable("getSeasonDetails")
    public Optional<TmdbSeasonDto> getSeasonDetails(Long tmdbId, Long season, Language language) {

        UriComponentsBuilder UrlBuilder = UriComponentsBuilder.fromHttpUrl(url + "tv/")
                .path(tmdbId.toString())
                .path("/season/")
                .path(season.toString())
                .queryParam("api_key", apikey)
                .queryParam("language", language.name());

        ResponseEntity<TmdbSeasonDto> response;
        try {
            response = restTemplate.getForEntity(UrlBuilder.toUriString(), TmdbSeasonDto.class);
        } catch (HttpStatusCodeException e) {
            log.error("Could not get tv season details by id: {}, season: {}, status: {}", tmdbId, season, e.getStatusCode().value());

            // if there are too many requests
            if (e.getStatusCode().value() == 429) {
                try {
                    // sleep current thread for 1s
                    sleep(1000);
                } catch (InterruptedException e1) {
                    log.error(e1.getMessage());
                }
                // and try again
                return this.getSeasonDetails(tmdbId, season, language);
            }
            return Optional.empty();
        }

        return Optional.ofNullable(response.getBody());
    }

    @Override
    @Cacheable("getEpisodeDetails")
    public Optional<TmdbEpisodeDto> getEpisodeDetails(Long tmdbId, Long season, Long episode, Language language) {
        UriComponentsBuilder UrlBuilder = UriComponentsBuilder.fromHttpUrl(url + "tv/")
                .path(tmdbId.toString())
                .path("/season/")
                .path(season.toString())
                .path("/episode/")
                .path(episode.toString())
                .queryParam("api_key", apikey)
                .queryParam("language", language.name());

        ResponseEntity<TmdbEpisodeDto> response;
        try {
            response = restTemplate.getForEntity(UrlBuilder.toUriString(), TmdbEpisodeDto.class);
        } catch (HttpStatusCodeException e) {
            log.error("Could not get tv episod details by id: {}, season: {}, episode: {}, status: {}", tmdbId, season, episode, e.getStatusCode().value());

            // if there are too many requests
            if (e.getStatusCode().value() == 429) {
                try {
                    // sleep current thread for 1s
                    sleep(1000);
                } catch (InterruptedException e1) {
                    log.error(e1.getMessage());
                }
                // and try again
                return this.getEpisodeDetails(tmdbId, season, episode, language);
            }
            return Optional.empty();
        }
        return Optional.ofNullable(response.getBody());
    }

    @Override
    @Cacheable("searchForTvSeries")
    public Optional<TmdbTVSearchDto> search(String query, Integer page, Long year, Language language) {
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(url + "search/tv")
                .queryParam("api_key", apikey)
                .queryParam("language", language.name())
                .queryParam("query", query)
                .queryParam("page", page == null ? 1 : page);

        if (year != null)
            urlBuilder.queryParam("first_air_date_year", year);

        ResponseEntity<TmdbTVSearchDto> response;
        try {
            response = restTemplate.getForEntity(urlBuilder.build(false).toUriString(), TmdbTVSearchDto.class);
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
                return this.search(query, page, year, language);
            }

            return Optional.empty();
        }
        return Optional.ofNullable(response.getBody());
    }
}
