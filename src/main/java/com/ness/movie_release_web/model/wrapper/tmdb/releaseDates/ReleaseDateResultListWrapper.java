package com.ness.movie_release_web.model.wrapper.tmdb.releaseDates;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReleaseDateResultListWrapper {
    @JsonProperty("results")
    private List<ReleaseDateResultWrapper> releaseDates = new ArrayList<>();
}
