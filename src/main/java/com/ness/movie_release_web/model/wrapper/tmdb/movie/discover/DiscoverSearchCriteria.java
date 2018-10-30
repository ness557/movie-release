package com.ness.movie_release_web.model.wrapper.tmdb.movie.discover;

import com.ness.movie_release_web.model.wrapper.tmdb.Language;
import com.ness.movie_release_web.model.wrapper.tmdb.releaseDates.ReleaseType;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

import static java.time.LocalDate.parse;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.trim;

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
    private List<LocalDate> dateRange = emptyList();
    private Double voteAverageMin = 0d;
    private Double voteAverageMax = 10d;
    private Integer page;
    private Language language;

    public LocalDate getReleaseDateMin() {
        if (dateRange != null && !dateRange.isEmpty()) {
            return dateRange.get(0);
        }
        return null;
    }

    public LocalDate getReleaseDateMax() {
        if (dateRange != null && !dateRange.isEmpty()) {
            return dateRange.get(1);
        }
        return null;
    }

    public void setDateRange(String dateRangeString) {
        this.dateRange = stream(dateRangeString.split("-"))
                .map(d -> parse(trim(d), ofPattern("MM/dd/yyyy")))
                .collect(toList());
    }

    public String getDateRange() {
        return String.join(" - ", dateRange.stream().map(d -> d.format(ofPattern("MM/dd/yyyy"))).collect(toList()));
    }
}
