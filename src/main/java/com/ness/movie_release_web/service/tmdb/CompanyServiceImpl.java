package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.model.dto.tmdb.Language;
import com.ness.movie_release_web.model.dto.tmdb.ProductionCompanyDto;
import com.ness.movie_release_web.model.dto.tmdb.company.search.CompanySearchDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
public class CompanyServiceImpl extends Cacheable<ProductionCompanyDto> implements CompanyService {

    private RestTemplate restTemplate = new RestTemplate();
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${tmdbapi.apikey}")
    private String apikey;

    @Value("${tmdbapi.url}")
    private String url;


    @Override
    public List<ProductionCompanyDto> getCompanies(List<Long> ids, Language language) {

        List<ProductionCompanyDto> result = new ArrayList<>();
        ids.forEach(id -> getCompany(id, language).ifPresent(result::add));
        return result;
    }

    @Override
    public Optional<ProductionCompanyDto> getCompany(Long id, Language language) {

        Optional<ProductionCompanyDto> fromCache = getFromCache(id, language);
        if (fromCache.isPresent()) {
            return fromCache;
        }

        UriComponentsBuilder movieBuilder = UriComponentsBuilder.fromHttpUrl(url + "company/")
                .path(id.toString())
                .queryParam("api_key", apikey)
                .queryParam("language", language);

        ResponseEntity<ProductionCompanyDto> response;
        try {
            response = restTemplate.getForEntity(movieBuilder.toUriString(), ProductionCompanyDto.class);
        } catch (HttpStatusCodeException e) {
            logger.error("Could not get companies: {}", e.getStatusCode().value());

            if (e.getStatusCode().value() == 429) {
                try {
                    // sleep current thread for 1s
                    sleep(1000);
                } catch (InterruptedException e1) {
                    logger.error(e1.getMessage());
                }
                // and try again
                return this.getCompany(id, language);
            }
            return Optional.empty();
        }

        putToCache(id, response.getBody(), language);

        return Optional.ofNullable(response.getBody());
    }

    @Override
    public Optional<CompanySearchDto> search(String query, Integer page, Language language) {
        UriComponentsBuilder movieBuilder = UriComponentsBuilder.fromHttpUrl(url + "search/company")
                .queryParam("query", query)
                .queryParam("page", page)
                .queryParam("api_key", apikey)
                .queryParam("language", language);

        ResponseEntity<CompanySearchDto> response;
        try {
            response = restTemplate.getForEntity(movieBuilder.toUriString(), CompanySearchDto.class);
        } catch (HttpStatusCodeException e) {
            logger.error("Could not get companies: {}", e.getStatusCode().value());

            if (e.getStatusCode().value() == 429) {
                try {
                    // sleep current thread for 1s
                    sleep(1000);
                } catch (InterruptedException e1) {
                    logger.error(e1.getMessage());
                }
                // and try again
                return this.search(query, page, language);
            }
            return Optional.empty();
        }

        return Optional.ofNullable(response.getBody());
    }
}
