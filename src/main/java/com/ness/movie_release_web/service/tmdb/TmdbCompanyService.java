package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.dto.Language;
import com.ness.movie_release_web.dto.tmdb.TmdbProductionCompanyDto;
import com.ness.movie_release_web.dto.tmdb.company.search.TmdbCompanySearchDto;

import java.util.List;
import java.util.Optional;

public interface TmdbCompanyService {
    List<TmdbProductionCompanyDto> getCompanies(List<Long> ids, Language language);
    Optional<TmdbProductionCompanyDto> getCompany(Long id, Language language);
    Optional<TmdbCompanySearchDto> search(String query, Integer page, Language language);
}
