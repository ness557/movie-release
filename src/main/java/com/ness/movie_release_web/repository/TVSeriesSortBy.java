package com.ness.movie_release_web.repository;

import org.springframework.data.domain.Sort;

public enum TVSeriesSortBy {

    NameEn_desc("nameEn", Sort.Direction.DESC),
    NameEn_asc("nameEn", Sort.Direction.ASC),
    NameRu_desc("nameRu", Sort.Direction.DESC),
    NameRu_asc("nameRu", Sort.Direction.ASC),
    Release_Date_desc("releaseDate", Sort.Direction.DESC),
    Release_Date_asc("releaseDate", Sort.Direction.ASC),
    Last_Episode_desc("lastEpisodeAirDate", Sort.Direction.DESC),
    Last_Episode_asc("lastEpisodeAirDate", Sort.Direction.ASC),
    Vote_average_desc("voteAverage", Sort.Direction.DESC),
    Vote_average_asc("voteAverage", Sort.Direction.ASC);

    private String type;
    private Sort.Direction order;

    TVSeriesSortBy(String type, Sort.Direction order) {
        this.type = type;
        this.order = order;
    }

    public String getType() {
        return type;
    }

    public Sort.Direction getOrder() {
        return order;
    }
}