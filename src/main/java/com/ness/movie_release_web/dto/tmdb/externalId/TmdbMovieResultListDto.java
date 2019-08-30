package com.ness.movie_release_web.dto.tmdb.externalId;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TmdbMovieResultListDto {
    @JsonProperty("movie_results")
    private List<TmdbMovieResultDto> movieResultList = new ArrayList<>();
}
