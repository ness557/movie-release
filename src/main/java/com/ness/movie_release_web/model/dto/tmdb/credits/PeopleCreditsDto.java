package com.ness.movie_release_web.model.dto.tmdb.credits;

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
public class PeopleCreditsDto {

    @JsonProperty("cast")
    List<PeopleCastDto> casts = new ArrayList<>();
    @JsonProperty("crew")
    List<PeopleCrewDto> crews = new ArrayList<>();
}
