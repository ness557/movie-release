package com.ness.movie_release_web.service;

import com.ness.movie_release_web.model.wrapper.OmdbFullWrapper;
import com.ness.movie_release_web.model.wrapper.OmdbSearchResultWrapper;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class FilmOmdbServiceImpl implements FilmOmdbService {

    private RestTemplate restTemplate = new RestTemplate();

    @Value("${omdbapi.url}")
    private String apiUrl;

    @Value("${omdbapi.apikey}")
    private String apikey;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public OmdbSearchResultWrapper search(String title, Integer year, Integer page) {

        // default search with title only
        Map values = new HashMap();
        values.put("apikey", apikey);
        values.put("url", apiUrl);
        values.put("title", title);

        StringBuilder builder = new StringBuilder();
        builder.append("${url}?");
        builder.append("apikey=${apikey}&");
        builder.append("s=${title}&");

        // request contains page number
        if (page != null) {
            builder.append("page=${page}&");
            values.put("page", page);
        }

        // req contains year of release
        if (year != null) {
            builder.append("y=${year}");
            values.put("year", year);
        }

        StrSubstitutor sub = new StrSubstitutor(values);
        String request = sub.replace(builder.toString());

        try {
            ResponseEntity<OmdbSearchResultWrapper> response = restTemplate.exchange(
                    request,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<OmdbSearchResultWrapper>() {
                    });

            OmdbSearchResultWrapper result = response.getBody();
            logger.info("Got films: {}", result);

            return result;
        } catch (RestClientException e) {
            e.printStackTrace();
            logger.error("Could not search for film {}", e.getMessage());
            return new OmdbSearchResultWrapper();
        }
    }

    @Override
    public OmdbFullWrapper getInfo(String imdbId) {
        Map values = new HashMap();
        values.put("apikey", apikey);
        values.put("url", apiUrl);
        values.put("imdbId", imdbId);

        String templateRequest = "${url}?apikey=${apikey}&i=${imdbId}";
        StrSubstitutor sub = new StrSubstitutor(values);
        String request = sub.replace(templateRequest);

        try {
            ResponseEntity<OmdbFullWrapper> response = restTemplate.exchange(
                    request,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<OmdbFullWrapper>() {
                    });


            OmdbFullWrapper fullWrapper = response.getBody();

            logger.info("Got fullFilmInfo: {}", fullWrapper);

            return fullWrapper;
        } catch (RestClientException e) {
            logger.error("Could not get full film Info: {}", e.getMessage());
            return new OmdbFullWrapper();
        }
    }
}
