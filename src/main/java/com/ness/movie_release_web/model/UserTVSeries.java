package com.ness.movie_release_web.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "uzer_tvseries")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class UserTVSeries {

    @EmbeddedId
    private UserTVSeriesPK id = new UserTVSeriesPK();

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("userId")
    @JoinColumn(name = "uzer_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @MapsId("tvSeriesId")
    @JoinColumn(name = "tvseries_id")
    private TVSeries tvSeries;

    @Column(name = "current_season_number")
    private Integer currentSeason;

    @Column(name = "current_episode_number")
    private Integer currentEpisode;
}
