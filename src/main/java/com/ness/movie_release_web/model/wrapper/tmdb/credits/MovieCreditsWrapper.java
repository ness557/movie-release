package com.ness.movie_release_web.model.wrapper.tmdb.credits;

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
public class MovieCreditsWrapper {

    @JsonProperty("cast")
    private List<MovieCastWrapper> casts = new ArrayList<>();
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("crew")
    private List<MovieCrewWrapper> crews = new ArrayList<>();
}
