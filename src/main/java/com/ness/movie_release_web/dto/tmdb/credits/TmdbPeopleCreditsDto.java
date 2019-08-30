package com.ness.movie_release_web.dto.tmdb.credits;

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
public class TmdbPeopleCreditsDto {

    @JsonProperty("cast")
    List<TmdbPeopleCastDto> casts = new ArrayList<>();
    @JsonProperty("crew")
    List<TmdbPeopleCrewDto> crews = new ArrayList<>();
}
