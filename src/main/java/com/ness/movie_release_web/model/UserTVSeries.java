package com.ness.movie_release_web.model;

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "uzer_tvseries")
@Data
@AllArgsConstructor
@NoArgsConstructor
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
    private Long currentSeason;

    @Column(name = "current_episode_number")
    private Long currentEpisode;
}
