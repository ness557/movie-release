package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.model.wrapper.tmdb.Language;
import com.ness.movie_release_web.model.wrapper.tmdb.people.PeopleWrapper;

import java.util.List;
import java.util.Optional;

public interface PeopleService {

    Optional<PeopleWrapper> getDetails(Integer id, Language language);
    List<PeopleWrapper> search(String query);
    List<PeopleWrapper> getPeopleList(List<Integer> people, Language language);
}
