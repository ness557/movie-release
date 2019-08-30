package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.dto.Language;
import com.ness.movie_release_web.dto.tmdb.people.TmdbPeopleDto;

import java.util.List;
import java.util.Optional;

public interface TmdbPeopleService {

    Optional<TmdbPeopleDto> getDetails(Long id, Language language);
    List<TmdbPeopleDto> search(String query);
    List<TmdbPeopleDto> getPeopleList(List<Long> people, Language language);
}
