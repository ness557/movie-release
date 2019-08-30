package com.ness.movie_release_web.service;

import com.ness.movie_release_web.model.TVSeries;
import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.model.UserTVSeries;
import com.ness.movie_release_web.model.UserTVSeriesPK;
import com.ness.movie_release_web.dto.Language;
import com.ness.movie_release_web.dto.tmdb.tvSeries.WatchStatus;
import com.ness.movie_release_web.dto.tmdb.tvSeries.details.TmdbSeasonDto;
import com.ness.movie_release_web.dto.tmdb.tvSeries.details.Status;
import com.ness.movie_release_web.dto.tmdb.tvSeries.details.TmdbTVDetailsDto;
import com.ness.movie_release_web.repository.TVSeriesRepository;
import com.ness.movie_release_web.repository.TVSeriesSortBy;
import com.ness.movie_release_web.repository.UserTVSeriesRepository;
import com.ness.movie_release_web.service.tmdb.TmdbTVSeriesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static com.ness.movie_release_web.repository.UserTVSeriesSpecifications.byUserAndTVStatusesAndWatchStatusesWithOrderby;

@Slf4j
@Service
@RequiredArgsConstructor
public class TVSeriesServiceImpl implements TVSeriesService {

    private final UserTVSeriesRepository userTVSeriesRepository;
    private final TVSeriesRepository tvSeriesRepository;
    private final TmdbTVSeriesService tvSeriesService;


    @Override
    public Boolean isExistsByTmdbIdAndUserId(Long tmdbId, Long userId) {
        return userTVSeriesRepository.existsById(UserTVSeriesPK.wrap(userId, tmdbId));
    }

    @Override
    public Optional<UserTVSeries> getByTmdbIdAndUserId(Long tmdbId, Long userId) {
        return userTVSeriesRepository.findById(UserTVSeriesPK.wrap(userId, tmdbId));
    }

    @Override
    public void setSeasonAndEpisode(Long tmdbId, User user, Long seasonNum, Long episodeNum) {

        Optional<TmdbTVDetailsDto> tvDetailsOptional = tvSeriesService.getTVDetails(tmdbId, Language.en);
        Optional<TmdbTVDetailsDto> tvDetailsOptionalRu = tvSeriesService.getTVDetails(tmdbId, Language.ru);

        if (!tvDetailsOptional.isPresent() || !tvDetailsOptionalRu.isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT);

        TmdbTVDetailsDto tvDetails = tvDetailsOptional.get();
        TmdbTVDetailsDto tvDetailsRu = tvDetailsOptionalRu.get();

        Optional<TVSeries> one = tvSeriesRepository.findById(tmdbId);

        Optional<UserTVSeries> userTVSeriesOptional = userTVSeriesRepository.findById(UserTVSeriesPK.wrap(user.getId(), tmdbId));
        UserTVSeries userTVSeries = userTVSeriesOptional.orElse(new UserTVSeries(
                new UserTVSeriesPK(),
                user,
                one.orElse(new TVSeries(
                        tmdbId,
                        tvDetails.getName(),
                        tvDetailsRu.getName(),
                        tvDetails.getFirstAirDate(),
                        tvDetails.getLastAirDate(),
                        tvDetails.getVoteAverage(),
                        0L,
                        0L,
                        tvDetails.getStatus())),
                seasonNum,
                episodeNum));

        userTVSeries.setCurrentSeason(seasonNum);
        userTVSeries.setCurrentEpisode(episodeNum);
        userTVSeriesRepository.save(userTVSeries);
    }

    @Override
    public Page<UserTVSeries> getByUserAndTVStatusesAndWatchStatusesWithOrderAndPages(
            List<Status> tvStatuses,
            List<WatchStatus> watchStatuses,
            TVSeriesSortBy sortBy,
            User user,
            Integer page,
            Integer size) {

        return userTVSeriesRepository.findAll(byUserAndTVStatusesAndWatchStatusesWithOrderby(
                tvStatuses, watchStatuses, sortBy, user),
                PageRequest.of(page, size));
    }

    @Override
    public List<UserTVSeries> getAllUserTVSeries() {
        return userTVSeriesRepository.findAll();
    }

    @Override
    public Long spentTotalMinutesToSeries(Long tmdbId, User user, Long currentSeason, Long currentEpisode) {

        Optional<TmdbTVDetailsDto> tvDetailsOptional = tvSeriesService.getTVDetails(tmdbId, user.getLanguage());
        if (!tvDetailsOptional.isPresent())
            return 0L;

        TmdbTVDetailsDto tvDetails = tvDetailsOptional.get();
        Double average = tvDetails.getEpisodeRunTime().stream().mapToDouble(Long::doubleValue).average().orElse(0d);
        Double result = 0d;


        for (int i = 1; i < currentSeason; i++) {
            Optional<TmdbSeasonDto> seasonDetailsOpt = tvSeriesService.getSeasonDetails(tmdbId, (long) i, user.getLanguage());
            if (!seasonDetailsOpt.isPresent())
                continue;

            TmdbSeasonDto tmdbSeasonDto = seasonDetailsOpt.get();
            result += tmdbSeasonDto.getEpisodes().size() * average;
        }

        Optional<TmdbSeasonDto> seasonDetails = tvSeriesService.getSeasonDetails(tmdbId, currentSeason, user.getLanguage());

        if (!seasonDetails.isPresent()) {
            return result.longValue();
        }
        TmdbSeasonDto tmdbSeasonDto = seasonDetails.get();

        result += tmdbSeasonDto.getEpisodes().stream().filter(e -> e.getEpisodeNumber() <= currentEpisode).count() * average;

        return result.longValue();
    }

    @Override
    public Long spentTotalMinutesToSeriesSeason(Long tmdbId, Long season, User user, Long currentSeason, Long currentEpisode) {

        Optional<TmdbTVDetailsDto> tvDetailsOptional = tvSeriesService.getTVDetails(tmdbId, user.getLanguage());
        if (!tvDetailsOptional.isPresent())
            return 0L;

        TmdbTVDetailsDto tvDetails = tvDetailsOptional.get();
        double average = tvDetails.getEpisodeRunTime().stream().mapToDouble(Long::doubleValue).average().orElse(0d);

        Optional<TmdbSeasonDto> seasonDetails = tvSeriesService.getSeasonDetails(tmdbId, currentSeason, user.getLanguage());

        if (season < currentSeason)
            return ((Double) (seasonDetails.orElse(new TmdbSeasonDto()).getEpisodes().size() * average)).longValue();

        if (season.equals(currentSeason))
            return ((Double) (seasonDetails.orElse(new TmdbSeasonDto()).getEpisodes().stream().filter(e -> e.getEpisodeNumber() <= currentEpisode).count() * average)).longValue();

        return 0L;
    }

    @Scheduled(cron = "${cron.pattern.updateDB}")
    @Override
    public void updateDB() {

        log.info("Updating series db...");
        tvSeriesRepository.findAll().forEach(tvs -> {
            Long id = tvs.getId();
            Optional<TmdbTVDetailsDto> tvDetails = tvSeriesService.getTVDetails(id, Language.en);

            if (!tvDetails.isPresent()) {
                return;
            }

            TmdbTVDetailsDto tmdbTvDetailsDto = tvDetails.get();
            tvs.setReleaseDate(tmdbTvDetailsDto.getFirstAirDate());
            tvs.setLastEpisodeAirDate(tmdbTvDetailsDto.getLastAirDate());
            tvs.setVoteAverage(tmdbTvDetailsDto.getVoteAverage());
            tvs.setNameEn(tmdbTvDetailsDto.getName());
            tvs.setStatus(tmdbTvDetailsDto.getStatus());

            Optional<TmdbTVDetailsDto> tvDetailsRu = tvSeriesService.getTVDetails(id, Language.ru);
            tvDetailsRu.ifPresent(tvDetailsDto1 -> tvs.setNameRu(tvDetailsDto1.getName()));

            Optional<TmdbSeasonDto> seasonDetails = tvSeriesService.getSeasonDetails(id, tmdbTvDetailsDto.getNumberOfSeasons(), Language.en);
            if (!seasonDetails.isPresent()) {
                tvSeriesRepository.save(tvs);
                return;
            }

            TmdbSeasonDto tmdbSeasonDto = seasonDetails.get();
            tmdbSeasonDto.getEpisodes().stream().min((s1, s2) -> ((Long) (s2.getEpisodeNumber() - s1.getEpisodeNumber())).intValue())
                    .ifPresent(e -> {
                        tvs.setLastSeasonNumber(e.getSeasonNumber());
                        tvs.setLastEpisodeNumber(e.getEpisodeNumber());
                    });

            tvSeriesRepository.save(tvs);
        });
        log.info("series db updated!");
    }

    @Override
    public Optional<TVSeries> findById(Long id) {
        return tvSeriesRepository.findById(id);
    }
}
