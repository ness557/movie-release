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
public class PeopleCreditsWrapper {

    @JsonProperty("cast")
    List<PeopleCastWrapper> casts = new ArrayList<>();
    @JsonProperty("crew")
    List<PeopleCrewWrapper> crews = new ArrayList<>();
}
