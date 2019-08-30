package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.dto.Language;
import com.ness.movie_release_web.dto.tmdb.TmdbProductionCompanyDto;
import com.ness.movie_release_web.dto.tmdb.company.search.TmdbCompanySearchDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.Thread.sleep;

@Service
@Slf4j
@RequiredArgsConstructor
public class TmdbCompanyServiceImpl implements TmdbCompanyService {

    private RestTemplate restTemplate = new RestTemplate();

    @Value("${tmdbapi.apikey}")
    private String apikey;

    @Value("${tmdbapi.url}")
    private String url;


    @Override
    public List<TmdbProductionCompanyDto> getCompanies(List<Long> ids, Language language) {

        List<TmdbProductionCompanyDto> result = new ArrayList<>();
        ids.forEach(id -> getCompany(id, language).ifPresent(result::add));
        return result;
    }

    @Override
    @Cacheable("getCompany")
    public Optional<TmdbProductionCompanyDto> getCompany(Long id, Language language) {

        UriComponentsBuilder movieBuilder = UriComponentsBuilder.fromHttpUrl(url + "company/")
                .path(id.toString())
                .queryParam("api_key", apikey)
                .queryParam("language", language);

        ResponseEntity<TmdbProductionCompanyDto> response;
        try {
            response = restTemplate.getForEntity(movieBuilder.toUriString(), TmdbProductionCompanyDto.class);
        } catch (HttpStatusCodeException e) {
            log.error("Could not get companies: {}", e.getStatusCode().value());

            if (e.getStatusCode().value() == 429) {
                try {
                    // sleep current thread for 1s
                    sleep(1000);
                } catch (InterruptedException e1) {
                    log.error(e1.getMessage());
                }
                // and try again
                return this.getCompany(id, language);
            }
            return Optional.empty();
        }

        return Optional.ofNullable(response.getBody());
    }

    @Override
    @Cacheable("search")
    public Optional<TmdbCompanySearchDto> search(String query, Integer page, Language language) {
        UriComponentsBuilder movieBuilder = UriComponentsBuilder.fromHttpUrl(url + "search/company")
                .queryParam("query", query)
                .queryParam("page", page)
                .queryParam("api_key", apikey)
                .queryParam("language", language);

        ResponseEntity<TmdbCompanySearchDto> response;
        try {
            response = restTemplate.getForEntity(movieBuilder.toUriString(), TmdbCompanySearchDto.class);
        } catch (HttpStatusCodeException e) {
            log.error("Could not get companies: {}", e.getStatusCode().value());

            if (e.getStatusCode().value() == 429) {
                try {
                    // sleep current thread for 1s
                    sleep(1000);
                } catch (InterruptedException e1) {
                    log.error(e1.getMessage());
                }
                // and try again
                return this.search(query, page, language);
            }
            return Optional.empty();
        }

        return Optional.ofNullable(response.getBody());
    }
}
