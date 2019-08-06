package com.ness.movie_release_web.service.tmdb.cache;

import com.ness.movie_release_web.model.dto.tmdb.Language;
import org.apache.commons.collections4.map.PassiveExpiringMap;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Scope("prototype")
public class CacheProvider<T> {

    private Map<Long, Map<Language, T>> cache;

    @PostConstruct
    public void init() {
        cache = new PassiveExpiringMap<>(3, TimeUnit.DAYS);
    }

    public Optional<T> getFromCache(Long id, Language language) {
        Map<Language, T> languageMap = cache.computeIfAbsent(id, k -> new HashMap<>());
        return Optional.ofNullable(languageMap.get(language));
    }

    public void putToCache(Long id, T object, Language language) {
        Map<Language, T> languageMap = cache.computeIfAbsent(id, k -> new HashMap<>());
        languageMap.put(language, object);
    }
}
