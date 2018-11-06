package com.ness.movie_release_web.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class UserTVSeriesPK implements Serializable {

    @Column(name = "uzer_id")
    private Long userId;

    @Column(name = "tvseries_id")
    private Long tvseriesId;
}
