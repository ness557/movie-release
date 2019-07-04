package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.model.dto.tmdb.Language;
import com.ness.movie_release_web.model.dto.tmdb.ProductionCompanyDto;
import com.ness.movie_release_web.model.dto.tmdb.company.search.CompanySearchDto;

import java.util.List;
import java.util.Optional;

public interface CompanyService {
    List<ProductionCompanyDto> getCompanies(List<Long> ids, Language language);
    Optional<ProductionCompanyDto> getCompany(Long id, Language language);
    Optional<CompanySearchDto> search(String query, Integer page, Language language);
}
