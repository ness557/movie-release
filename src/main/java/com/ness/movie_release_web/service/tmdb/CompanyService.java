package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.model.wrapper.tmdb.Language;
import com.ness.movie_release_web.model.wrapper.tmdb.ProductionCompanyWrapper;
import com.ness.movie_release_web.model.wrapper.tmdb.company.search.CompanySearchWrapper;

import java.util.List;
import java.util.Optional;

public interface CompanyService {
    List<ProductionCompanyWrapper> getCompanies(List<Long> ids, Language language);
    Optional<ProductionCompanyWrapper> getCompany(Long id, Language language);
    Optional<CompanySearchWrapper> search(String query, Integer page, Language language);
}
