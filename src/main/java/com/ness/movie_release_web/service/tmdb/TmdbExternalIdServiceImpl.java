package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.dto.tmdb.externalId.TmdbMovieResultListDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import static com.ness.movie_release_web.util.tmdb.TmdbApiUtils.getTmdbEntity;

@Service
@Slf4j
public class TmdbExternalIdServiceImpl implements TmdbExternalIdService {

    private RestTemplate restTemplate = new RestTemplate();

    @Value("${tmdbapi.apikey}")
    private String apikey;

    @Value("${tmdbapi.url}")
    private String url;

    @Override
    @Cacheable("getTmdbIdByImdbId")
    public Long getTmdbIdByImdbId(String imdbId) {
        UriComponentsBuilder movieBuilder = UriComponentsBuilder.fromHttpUrl(url + "find/")
                .path(imdbId)
                .queryParam("external_source", "imdb_id")
                .queryParam("api_key", apikey);

        return getTmdbEntity(movieBuilder.toUriString(), restTemplate, TmdbMovieResultListDto.class)
                .map(dto -> dto.getMovieResultList().get(0).getId())
                .orElse(0L);
    }
}
