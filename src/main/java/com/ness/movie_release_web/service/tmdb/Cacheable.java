package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.model.wrapper.tmdb.Language;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

public abstract class Cacheable<T> {

    private Map<Integer, Map<Language, T>> cache = new WeakHashMap<>();

    protected Optional<T> getFromCache(Integer id, Language language){
        Map<Language, T> languageMap = cache.computeIfAbsent(id, k -> new HashMap<>());
        return Optional.ofNullable(languageMap.get(language));
    }

    protected void putToCache(Integer id, T object, Language language){
        Map<Language, T> languageMap = cache.computeIfAbsent(id, k -> new HashMap<>());
        languageMap.put(language, object);
    }
}
