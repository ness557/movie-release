package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.model.wrapper.tmdb.ProductionCompanyWrapper;

import java.util.List;

public interface NetworkService {
    List<ProductionCompanyWrapper> getNetworks();
    List<ProductionCompanyWrapper> getNetworks(List<Integer> ids);
    List<ProductionCompanyWrapper> search(String query);
}
