package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.model.dto.tmdb.Language;
import com.ness.movie_release_web.model.dto.tmdb.people.PeopleDto;

import java.util.List;
import java.util.Optional;

public interface PeopleService {

    Optional<PeopleDto> getDetails(Long id, Language language);
    List<PeopleDto> search(String query);
    List<PeopleDto> getPeopleList(List<Long> people, Language language);
}
