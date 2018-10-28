package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.model.wrapper.tmdb.Language;
import com.ness.movie_release_web.model.wrapper.tmdb.movie.details.ProductionCompany;
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
public class CompanyServiceImpl implements CompanyService {

    private RestTemplate restTemplate = new RestTemplate();
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${tmdbapi.apikey}")
    private String apikey;

    @Value("${tmdbapi.url}")
    private String url;


    @Override
    public List<ProductionCompany> getCompanies(List<Integer> ids, Language language) {

        List<ProductionCompany> result = new ArrayList<>();
        ids.forEach(id -> getCompany(id, language).ifPresent(result::add));
        return result;
    }

    @Override
    public Optional<ProductionCompany> getCompany(Integer id, Language language) {
        UriComponentsBuilder movieBuilder = UriComponentsBuilder.fromHttpUrl(url + "company/")
                .path(id.toString())
                .queryParam("api_key", apikey)
                .queryParam("language", language);

        ResponseEntity<ProductionCompany> response = null;
        try {
            response = restTemplate.getForEntity(movieBuilder.toUriString(), ProductionCompany.class);
        } catch (HttpStatusCodeException e) {
            logger.error("Could not get genres: {}", e.getStatusCode().value());

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

        return Optional.ofNullable(response.getBody());
    }
}
