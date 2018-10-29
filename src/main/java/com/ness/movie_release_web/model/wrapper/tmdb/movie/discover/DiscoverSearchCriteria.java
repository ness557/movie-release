package com.ness.movie_release_web.model.wrapper.tmdb.movie.discover;

import com.ness.movie_release_web.model.wrapper.tmdb.Language;
import com.ness.movie_release_web.model.wrapper.tmdb.releaseDates.ReleaseType;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

import static java.util.Collections.emptyList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class DiscoverSearchCriteria {

    private List<Integer> genres = emptyList();
    private Boolean genresAnd = false;
    private List<Integer> companies = emptyList();
    private Boolean companiesAnd = false;
    private List<ReleaseType> releaseTypes = emptyList();
    private Boolean releaseTypeAnd = false;
    private SortBy sortBy = SortBy.popularity_desc;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDateMin = LocalDate.of(1800, 1, 1);
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDateMax = LocalDate.of(2200, 1, 1);
    private Double voteAverageMin = 0d;
    private Double voteAverageMax = 10d;
    private Integer page;
    private Language language;
}
