package com.ness.movie_release_web.model.wrapper.tmdb.movie.discover;

import com.ness.movie_release_web.model.wrapper.tmdb.Language;
import com.ness.movie_release_web.model.wrapper.tmdb.releaseDates.ReleaseType;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class DiscoverSearchCriteria {

    private List<Integer> genres;
    private List<Integer> companies;
    private List<ReleaseType> releaseType;
    private SortBy sortBy;
    private LocalDate releaseDateMin;
    private LocalDate releaseDateMax;
    private Double voteAverageMin;
    private Double voteAverageMax;
    private Integer page;
    private Language language;
}
