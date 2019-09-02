package com.ness.movie_release_web.service.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ness.movie_release_web.dto.Language;
import com.ness.movie_release_web.dto.tmdb.TmdbGenreDto;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

import static com.ness.movie_release_web.util.tmdb.TmdbApiUtils.getTmdbEntity;
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
    @Cacheable("getMovieGenres")
    public List<TmdbGenreDto> getMovieGenres(Language language) {

        UriComponentsBuilder movieBuilder = UriComponentsBuilder.fromHttpUrl(url + "genre/movie/list")
                .queryParam("api_key", apikey)
                .queryParam("language", language);

        return getTmdbEntity(movieBuilder.toUriString(), restTemplate, GenreList.class)
                .map(GenreList::getGenres)
                .orElse(emptyList());
    }

    @Override
    @Cacheable("getTVGenres")
    public List<TmdbGenreDto> getTVGenres(Language language) {
        UriComponentsBuilder movieBuilder = UriComponentsBuilder.fromHttpUrl(url + "genre/tv/list")
                .queryParam("api_key", apikey)
                .queryParam("language", language);

        return getTmdbEntity(movieBuilder.toUriString(), restTemplate, GenreList.class)
                .map(GenreList::getGenres)
                .orElse(emptyList());
    }
}

@Getter
class GenreList{
    @JsonProperty("genres")
    private List<TmdbGenreDto> genres = new ArrayList<>();
}
