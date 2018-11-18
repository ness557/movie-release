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
public class TVCreditsWrapper {

    @JsonProperty("cast")
    private List<TVCastWrapper> casts = new ArrayList<>();
    @JsonProperty("crew")
    private List<TVCrewWrapper> crews = new ArrayList<>();
}
