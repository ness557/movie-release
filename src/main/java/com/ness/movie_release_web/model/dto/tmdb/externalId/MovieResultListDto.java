package com.ness.movie_release_web.model.dto.tmdb.externalId;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MovieResultListDto {
    @JsonProperty("movie_results")
    private List<MovieResultDto> movieResultList = new ArrayList<>();
}
