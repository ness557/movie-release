package com.ness.movie_release_web.dto.tmdb.releaseDates;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TmdbReleaseDateResultListDto {
    @JsonProperty("results")
    private List<TmdbReleaseDateResultDto> releaseDates = new ArrayList<>();
}
