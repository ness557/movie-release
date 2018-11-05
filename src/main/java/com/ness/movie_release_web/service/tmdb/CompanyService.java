package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.model.wrapper.tmdb.Language;
import com.ness.movie_release_web.model.wrapper.tmdb.movie.details.ProductionCompanyWrapper;
import com.ness.movie_release_web.model.wrapper.tmdb.movie.search.CompanySearchWrapper;

import java.util.List;
import java.util.Optional;

public interface CompanyService {
    List<ProductionCompanyWrapper> getCompanies(List<Integer> ids, Language language);
    Optional<ProductionCompanyWrapper> getCompany(Integer id, Language language);
    Optional<CompanySearchWrapper> search(String query, Integer page, Language language);
}
