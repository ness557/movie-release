package com.ness.movie_release_web.service.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ness.movie_release_web.dto.Language;
import com.ness.movie_release_web.dto.tmdb.people.TmdbPeopleDto;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.ness.movie_release_web.util.tmdb.TmdbApiUtils.getTmdbEntity;

@Service
@Slf4j
public class TmdbPeopleServiceImpl implements TmdbPeopleService {

    private RestTemplate restTemplate = new RestTemplate();

    @Value("${tmdbapi.apikey}")
    private String apikey;

    @Value("${tmdbapi.url}")
    private String url;

    @Override
    @Cacheable("getPeopleList")
    public List<TmdbPeopleDto> getPeopleList(List<Long> people, Language language) {

        List<TmdbPeopleDto> result = new ArrayList<>();
        people.forEach(id -> getDetails(id, language).ifPresent(result::add));
        return result;
    }

    @Override
    @Cacheable("getPeopleDetails")
    public Optional<TmdbPeopleDto> getDetails(Long id, Language language) {

        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(url + "person/")
                .path(id.toString())
                .queryParam("api_key", apikey)
                .queryParam("language", language.name())
                .queryParam("append_to_response", "movie_credits,tv_credits");

        return getTmdbEntity(urlBuilder.toUriString(), restTemplate, TmdbPeopleDto.class);
    }

    @Override
    @Cacheable("peopleSearch")
    public List<TmdbPeopleDto> search(String query) {

        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(url + "search/person")
                .queryParam("query", query)
                .queryParam("api_key", apikey);

        return getTmdbEntity(urlBuilder.toUriString(), restTemplate, SearchResult.class)
                .map(SearchResult::getResults)
                .orElse(Collections.emptyList());
    }
}

@Getter
class SearchResult {
    @JsonProperty("results")
    private List<TmdbPeopleDto> results = new ArrayList<>();
}
