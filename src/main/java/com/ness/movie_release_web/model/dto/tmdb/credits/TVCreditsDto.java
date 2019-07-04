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
public class TVCreditsDto {

    @JsonProperty("cast")
    private List<TVCastDto> casts = new ArrayList<>();
    @JsonProperty("crew")
    private List<TVCrewDto> crews = new ArrayList<>();
}
