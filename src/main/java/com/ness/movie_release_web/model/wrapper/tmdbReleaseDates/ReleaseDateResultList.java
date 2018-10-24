package com.ness.movie_release_web.model.wrapper.tmdbReleaseDates;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReleaseDateResultList {
    @JsonProperty("results")
    private List<ReleaseDateResult> releaseDates = new ArrayList<>();
}
