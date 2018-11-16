package com.ness.movie_release_web.repository;

import com.ness.movie_release_web.model.Film;
import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.model.wrapper.tmdb.movie.details.Status;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.List;

public class FilmSpecifications {

    public static Specification<Film> byUserAndStatusWithOrderby(List<Status> statuses,
                                                                 MovieSortBy sortBy,
                                                                 User user){

        return (Specification<Film>) (root, criteriaQuery, cb) -> {

            Join<Object, Object> joinUser = root.join("users");

            switch (sortBy.getOrder()) {
                case ASC:
                    criteriaQuery.orderBy(cb.asc(root.get(sortBy.getType())));
                    break;

                case DESC:
                    criteriaQuery.orderBy(cb.desc(root.get(sortBy.getType())));
                    break;
            }

            Predicate result = cb.equal(joinUser.get("id"), user.getId());

            if (!statuses.isEmpty()) {
                result = cb.and(result, root.get("status").in(statuses));
            }

            return result;

        };
    }
}