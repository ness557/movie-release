package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.dto.tmdb.TmdbProductionCompanyDto;

import java.util.List;

public interface TmdbNetworkService {
    List<TmdbProductionCompanyDto> getNetworks();
    List<TmdbProductionCompanyDto> getNetworks(List<Long> ids);
    List<TmdbProductionCompanyDto> search(String query);
}
