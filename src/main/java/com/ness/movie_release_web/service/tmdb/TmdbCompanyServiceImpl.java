package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.dto.Language;
import com.ness.movie_release_web.dto.tmdb.TmdbProductionCompanyDto;
import com.ness.movie_release_web.dto.tmdb.company.search.TmdbCompanySearchDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.ness.movie_release_web.util.tmdb.TmdbApiUtils.getTmdbEntity;

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
    @Cacheable("getCompanies")
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

        return getTmdbEntity(movieBuilder.toUriString(), restTemplate, TmdbProductionCompanyDto.class);
    }

    @Override
    @Cacheable("companySearch")
    public Optional<TmdbCompanySearchDto> search(String query, Integer page, Language language) {
        UriComponentsBuilder movieBuilder = UriComponentsBuilder.fromHttpUrl(url + "search/company")
                .queryParam("query", query)
                .queryParam("page", page)
                .queryParam("api_key", apikey)
                .queryParam("language", language);

        return getTmdbEntity(movieBuilder.toUriString(), restTemplate, TmdbCompanySearchDto.class);
    }
}
