package com.ness.movie_release_web.model;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tv_series")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TVSeries {

    @Id
    @Column(name = "tmdb_id")
    private Long id;

    @OneToMany(mappedBy = "user")
    private Set<UserTVSeries> userTVSeries = new HashSet<>();
}
