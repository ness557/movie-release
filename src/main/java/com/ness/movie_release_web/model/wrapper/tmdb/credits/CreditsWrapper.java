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
public class CreditsWrapper {

    @JsonProperty("cast")
    List<CastWrapper> casts = new ArrayList<>();
    @JsonProperty("crew")
    List<CrewWrapper> crews = new ArrayList<>();
}
