package com.ness.movie_release_web.repository;

import org.springframework.data.domain.Sort;

public enum MovieSortBy {

    NameEn_desc("nameEn", Sort.Direction.DESC),
    NameEn_asc("nameEn", Sort.Direction.ASC),
    NameRu_desc("nameRu", Sort.Direction.DESC),
    NameRu_asc("nameRu", Sort.Direction.ASC),
    Release_Date_desc("releaseDate", Sort.Direction.DESC),
    Release_Date_asc("releaseDate", Sort.Direction.ASC),
    Vote_average_desc("voteAverage", Sort.Direction.DESC),
    Vote_average_asc("voteAverage", Sort.Direction.ASC);

    private String type;
    private Sort.Direction order;

    MovieSortBy(String type, Sort.Direction order) {
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
