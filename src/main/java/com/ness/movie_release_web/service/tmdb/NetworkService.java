package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.model.dto.tmdb.ProductionCompanyDto;

import java.util.List;

public interface NetworkService {
    List<ProductionCompanyDto> getNetworks();
    List<ProductionCompanyDto> getNetworks(List<Long> ids);
    List<ProductionCompanyDto> search(String query);
}
