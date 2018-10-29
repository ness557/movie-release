package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.model.wrapper.tmdb.Language;
import com.ness.movie_release_web.model.wrapper.tmdb.movie.details.ProductionCompany;
import com.ness.movie_release_web.model.wrapper.tmdb.movie.search.CompanySearch;

import java.util.List;
import java.util.Optional;

public interface CompanyService {
    List<ProductionCompany> getCompanies(List<Integer> ids, Language language);
    Optional<ProductionCompany> getCompany(Integer id, Language language);
    Optional<CompanySearch> search(String query, Integer page, Language language);
}
