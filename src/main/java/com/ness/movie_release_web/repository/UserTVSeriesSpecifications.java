package com.ness.movie_release_web.repository;

import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.model.UserTVSeries;
import com.ness.movie_release_web.model.dto.tmdb.tvSeries.WatchStatus;
import com.ness.movie_release_web.model.dto.tmdb.tvSeries.details.Status;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class UserTVSeriesSpecifications {

    public static Specification<UserTVSeries> byUserAndTVStatusesAndWatchStatusesWithOrderby(List<Status> tvStatuses,
                                                                                             List<WatchStatus> watchStatuses,
                                                                                             TVSeriesSortBy sortBy,
                                                                                             User user) {
        return (Specification<UserTVSeries>) (root, criteriaQuery, cb) -> {

            Join<Object, Object> tvSeriesJoin = root.join("tvSeries");
            Join<Object, Object> userJoin = root.join("user");

            List<Predicate> watchPredicates = new ArrayList<>();

            if (watchStatuses.contains(WatchStatus.NOT_STARTED))
                watchPredicates.add(
                        cb.and(
                                cb.equal(root.get("currentSeason"), 0),
                                cb.equal(root.get("currentEpisode"), 0)));

            if (watchStatuses.contains(WatchStatus.IN_PROGRESS)) {
                watchPredicates.add(
                        cb.and(
                                cb.or(
                                        cb.gt(root.get("currentEpisode"), 0),
                                        cb.gt(root.get("currentSeason"), 0)
                                ),
                                cb.or(
                                        cb.lt(root.get("currentSeason"), tvSeriesJoin.get("lastSeasonNumber")),
                                        cb.lt(root.get("currentEpisode"), tvSeriesJoin.get("lastEpisodeNumber"))
                                )
                        )
                );
            }

            if (watchStatuses.contains(WatchStatus.WATCHED)) {
                watchPredicates.add(
                        cb.and(
                                cb.equal(root.get("currentSeason"), tvSeriesJoin.get("lastSeasonNumber")),
                                cb.equal(root.get("currentEpisode"), tvSeriesJoin.get("lastEpisodeNumber"))
                        )
                );
            }

            Predicate result = cb.equal(userJoin.get("id"), user.getId());

            switch (sortBy.getOrder()) {
                case ASC:
                    criteriaQuery.orderBy(cb.asc(tvSeriesJoin.get(sortBy.getType())));
                    break;

                case DESC:
                    criteriaQuery.orderBy(cb.desc(tvSeriesJoin.get(sortBy.getType())));
                    break;
            }

            if (!tvStatuses.isEmpty()) {
                result = cb.and(result, tvSeriesJoin.get("status").in(tvStatuses));
            }

            if (!watchPredicates.isEmpty()) {

                Predicate watchPredicate = null;

                for (Predicate wp : watchPredicates) {
                    if (watchPredicate == null) {
                        watchPredicate = wp;
                        continue;
                    }
                    watchPredicate = cb.or(watchPredicate, wp);
                }

                result = cb.and(result, watchPredicate);

            }

            return result;
        };
    }
}
