package com.ness.movie_release_web.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class UserTVSeriesPK implements Serializable {

    public static UserTVSeriesPK wrap(Long userId, Long tvseriesId){
        UserTVSeriesPK userTVSeriesPK = new UserTVSeriesPK();
        userTVSeriesPK.setUserId(userId);
        userTVSeriesPK.setTvSeriesId(tvseriesId);
        return userTVSeriesPK;
    }

    @Column(name = "uzer_id")
    private Long userId;

    @Column(name = "tvseries_id")
    private Long tvSeriesId;
}
