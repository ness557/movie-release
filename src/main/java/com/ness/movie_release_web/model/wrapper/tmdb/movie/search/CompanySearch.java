package com.ness.movie_release_web.model.wrapper.tmdb.movie.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ness.movie_release_web.model.wrapper.tmdb.movie.details.ProductionCompany;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class CompanySearch {

    @JsonProperty("page")
    private Integer page;
    @JsonProperty("total_results")
    private Integer totalResults;
    @JsonProperty("total_pages")
    private Integer totalPages;
    @JsonProperty("results")
    private List<ProductionCompany> results = new ArrayList<>();
}