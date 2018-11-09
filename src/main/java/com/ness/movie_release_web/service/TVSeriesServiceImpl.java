package com.ness.movie_release_web.service;

import com.ness.movie_release_web.model.TVSeries;
import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.model.UserTVSeries;
import com.ness.movie_release_web.model.UserTVSeriesPK;
import com.ness.movie_release_web.model.wrapper.tmdb.Language;
import com.ness.movie_release_web.model.wrapper.tmdb.tvSeries.details.SeasonWrapper;
import com.ness.movie_release_web.model.wrapper.tmdb.tvSeries.details.TVDetailsWrapper;
import com.ness.movie_release_web.repository.TVSeriesRepository;
import com.ness.movie_release_web.repository.UserTVSeriesRepository;
import com.ness.movie_release_web.service.tmdb.TmdbTVSeriesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Slf4j
@Service
public class TVSeriesServiceImpl implements TVSeriesService {

    @Autowired
    private UserTVSeriesRepository userTVSeriesRepository;

    @Autowired
    private TVSeriesRepository tvSeriesRepository;

    @Autowired
    private TmdbTVSeriesService tvSeriesService;


    @Override
    public Boolean isExistsByTmdbIdAndUserId(Integer tmdbId, Long userId) {
        return userTVSeriesRepository.existsById(UserTVSeriesPK.wrap(userId, tmdbId.longValue()));
    }

    @Override
    public Optional<UserTVSeries> getByTmdbIdAndUserId(Integer tmdbId, Long userId) {
        return userTVSeriesRepository.findById(UserTVSeriesPK.wrap(userId, tmdbId.longValue()));
    }

    @Override
    public void subscribeUser(Integer tmdbId, User user) {


        if (userTVSeriesRepository.existsById(UserTVSeriesPK.wrap(user.getId(), tmdbId.longValue()))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        Optional<TVDetailsWrapper> tvDetailsOptional = tvSeriesService.getTVDetails(tmdbId, Language.en);

        if (!tvDetailsOptional.isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT);

        Optional<TVSeries> one = tvSeriesRepository.findById(tmdbId.longValue());

        UserTVSeries userTVSeries = new UserTVSeries();
        userTVSeries.setTvSeries(one.orElse(new TVSeries(tmdbId.longValue())));
        userTVSeries.setUser(user);
        userTVSeries.setCurrentSeason(0);
        userTVSeries.setCurrentEpisode(0);

        userTVSeriesRepository.save(userTVSeries);
    }

    @Override
    public void unSubscribeUser(Integer tmdbId, User user) {

        Optional<UserTVSeries> userTVSeries = userTVSeriesRepository.findById(UserTVSeriesPK.wrap(user.getId(), tmdbId.longValue()));

        if (!userTVSeries.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        userTVSeriesRepository.delete(userTVSeries.get());
    }

    @Override
    public Page<UserTVSeries> getAllByUserWithPages(Integer page, Integer size, User user) {
        return userTVSeriesRepository.findAllByUserOrderByTvSeriesIdDesc(user, PageRequest.of(page, size));
    }

    @Override
    public Long spentTotalMinutesToSeries(Integer tmdbId, User user) {
        Optional<UserTVSeries> byId = userTVSeriesRepository.findById(UserTVSeriesPK.wrap(user.getId(), tmdbId.longValue()));
        if (!byId.isPresent())
            return 0L;


        UserTVSeries userTVSeries = byId.get();
        Integer currentSeason = userTVSeries.getCurrentSeason();
        Integer currentEpisode = userTVSeries.getCurrentEpisode();

        Optional<TVDetailsWrapper> tvDetailsOptional = tvSeriesService.getTVDetails(tmdbId, user.getLanguage());
        if (!tvDetailsOptional.isPresent())
            return 0L;

        TVDetailsWrapper tvDetails = tvDetailsOptional.get();
        Double average = tvDetails.getEpisodeRunTime().stream().mapToDouble(Integer::doubleValue).average().orElse(0d);
        Double result = 0d;


        for(int i = 1; i < currentSeason; i++){
            Optional<SeasonWrapper> seasonDetailsOpt = tvSeriesService.getSeasonDetails(tmdbId, i, user.getLanguage());
            if (!seasonDetailsOpt.isPresent())
                continue;

            SeasonWrapper seasonWrapper = seasonDetailsOpt.get();
            result += seasonWrapper.getEpisodes().size() * average;
        }

        Optional<SeasonWrapper> seasonDetails = tvSeriesService.getSeasonDetails(tmdbId, currentSeason, user.getLanguage());

        if (!seasonDetails.isPresent()) {
            return result.longValue();
        }
        SeasonWrapper seasonWrapper = seasonDetails.get();

        result += seasonWrapper.getEpisodes().stream().filter(e -> e.getEpisodeNumber() <= currentEpisode).count() * average;

        return result.longValue();
    }
}
