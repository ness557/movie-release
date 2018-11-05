package com.ness.movie_release_web.model.wrapper.tmdb.externalId;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MovieResultListWrapper {
    @JsonProperty("movie_results")
    private List<MovieResultWrapper> movieResultWrapperList = new ArrayList<>();
}
