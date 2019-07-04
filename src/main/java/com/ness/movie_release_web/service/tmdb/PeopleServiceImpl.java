package com.ness.movie_release_web.service.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ness.movie_release_web.model.dto.tmdb.Language;
import com.ness.movie_release_web.model.dto.tmdb.people.PeopleDto;
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
public class PeopleServiceImpl implements PeopleService {

    private RestTemplate restTemplate = new RestTemplate();

    @Value("${tmdbapi.apikey}")
    private String apikey;

    @Value("${tmdbapi.url}")
    private String url;

    @Override
    public List<PeopleDto> getPeopleList(List<Long> people, Language language) {

        List<PeopleDto> result = new ArrayList<>();
        people.forEach(id -> getDetails(id, language).ifPresent(result::add));
        return result;
    }

    @Override
    public Optional<PeopleDto> getDetails(Long id, Language language) {

        UriComponentsBuilder UrlBuilder = UriComponentsBuilder.fromHttpUrl(url + "person/")
                .path(id.toString())
                .queryParam("api_key", apikey)
                .queryParam("language", language.name())
                .queryParam("append_to_response", "credits,tv_credits");


        ResponseEntity<PeopleDto> response;
        try {
            response = restTemplate.getForEntity(UrlBuilder.toUriString(), PeopleDto.class);
        } catch (HttpStatusCodeException e) {
            log.error("Could not get people details by id: {}, status: {}", id, e.getStatusCode().value());

            // if there are too many requests
            if (e.getStatusCode().value() == 429) {
                try {
                    // sleep current thread for 1s
                    sleep(1000);
                } catch (InterruptedException e1) {
                    log.error(e1.getMessage());
                }
                // and try again
                return this.getDetails(id, language);
            }
            return Optional.empty();
        }

        return Optional.ofNullable(response.getBody());
    }

    @Override
    public List<PeopleDto> search(String query) {

        UriComponentsBuilder UrlBuilder = UriComponentsBuilder.fromHttpUrl(url + "search/person")
                .queryParam("query", query)
                .queryParam("api_key", apikey);

        ResponseEntity<SearchResult> response;

        try {
            response = restTemplate.getForEntity(UrlBuilder.toUriString(), SearchResult.class);
        } catch (HttpStatusCodeException e) {
            log.error("Could not get search for people: {}, status: {}", query, e.getStatusCode().value());

            // if there are too many requests
            if (e.getStatusCode().value() == 429) {
                try {
                    // sleep current thread for 1s
                    sleep(1000);
                } catch (InterruptedException e1) {
                    log.error(e1.getMessage());
                }
                // and try again
                return this.search(query);
            }
            return emptyList();
        }

        return Optional.ofNullable(response.getBody()).map(SearchResult::getResults).orElse(Collections.emptyList());
    }
}

@Getter
class SearchResult {
    @JsonProperty("results")
    private List<PeopleDto> results = new ArrayList<>();
}
