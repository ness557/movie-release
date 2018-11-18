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
    private List<Integer> networks = emptyList();
    private Boolean networksAnd = false;
    private List<Integer> people = emptyList();
    private Boolean peopleAnd = false;
    private List<Integer> companies = emptyList();
    private Boolean companiesAnd = false;
    private List<ReleaseType> releaseTypes = emptyList();
    private Boolean releaseTypeAnd = false;
    private SortBy sortBy = SortBy.popularity_desc;
    private LocalDate releaseDateMax;
    private LocalDate releaseDateMin;
    //    private List<LocalDate> dateRange = emptyList();
    private Double voteAverageMin = 0d;
    private Double voteAverageMax = 10d;
    private Integer page;
    private Language language;

    public LocalDate getReleaseDateMin() {
        return releaseDateMin;
    }

    public LocalDate getReleaseDateMax() {
        return releaseDateMax;
    }

    public void setDateRange(String dateRangeString) {
        List<LocalDate> dates = stream(dateRangeString.split("-"))
                .map(d -> parse(trim(d), ofPattern("dd.MM.yyyy")))
                .collect(toList());
        releaseDateMin = dates.get(0);
        releaseDateMax = dates.get(1);
    }

    public void setVoteAverageStr(String str){
        String[] split = str.split(",");
        voteAverageMin = Double.valueOf(split[0]);
        voteAverageMax = Double.valueOf(split[1]);
    }

    public String getVoteAverageStr(){
        return String.join(",", voteAverageMin.toString(), voteAverageMax.toString());
    }

    public Double[] getVoteAverageArray(){
        return new Double[]{voteAverageMin, voteAverageMax};
    }

    public String getDateRange() {
        if (releaseDateMin != null && releaseDateMax != null)
            return releaseDateMin.format(ofPattern("dd.MM.yyyy")) + " - " + releaseDateMax.format(ofPattern("dd.MM.yyyy"));
        return LocalDate.now().minusYears(3).format(ofPattern("dd.MM.yyyy")) + " - " + LocalDate.now().format(ofPattern("dd.MM.yyyy"));
    }
}
