package com.ness.movie_release_web.dto.tmdb.movie.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class TmdbMovieSearchDto {

    @JsonProperty("page")
    private Integer page;
    @JsonProperty("total_results")
    private Long totalResults;
    @JsonProperty("total_pages")
    private Long totalPages;
    @JsonProperty("results")
    private List<TmdbMovieDto> results = new ArrayList<>();
}
