package com.ness.movie_release_web.model;

import com.ness.movie_release_web.model.wrapper.tmdb.tvSeries.details.Status;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tv_series")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class TVSeries {

    @Id
    @Column(name = "tmdb_id")
    private Long id;

    @Column(name = "name_en")
    private String nameEn;

    @Column(name = "name_ru")
    private String nameRu;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "last_episode_air_date")
    private LocalDate lastEpisodeAirDate;

    @Column(name = "vote_average")
    private Float voteAverage;

    @Column(name = "last_season_number")
    private Integer lastSeasonNumber;

    @Column(name = "last_episode_number")
    private Integer lastEpisodeNumber;

    @Enumerated
    private Status status;

    public TVSeries(Long id){
        this.id = id;
    }
}
