package com.ness.movie_release_web.model.wrapper.tmdb.company.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ness.movie_release_web.model.wrapper.tmdb.ProductionCompanyWrapper;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class CompanySearchWrapper {

    @JsonProperty("page")
    private Integer page;
    @JsonProperty("total_results")
    private Long totalResults;
    @JsonProperty("total_pages")
    private Long totalPages;
    @JsonProperty("results")
    private List<ProductionCompanyWrapper> results = new ArrayList<>();
}
