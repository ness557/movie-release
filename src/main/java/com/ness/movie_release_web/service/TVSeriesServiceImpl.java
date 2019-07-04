package com.ness.movie_release_web.service;

import com.ness.movie_release_web.model.TVSeries;
import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.model.UserTVSeries;
import com.ness.movie_release_web.model.UserTVSeriesPK;
import com.ness.movie_release_web.model.dto.tmdb.Language;
import com.ness.movie_release_web.model.dto.tmdb.tvSeries.WatchStatus;
import com.ness.movie_release_web.model.dto.tmdb.tvSeries.details.SeasonDto;
import com.ness.movie_release_web.model.dto.tmdb.tvSeries.details.Status;
import com.ness.movie_release_web.model.dto.tmdb.tvSeries.details.TVDetailsDto;
import com.ness.movie_release_web.repository.TVSeriesRepository;
import com.ness.movie_release_web.repository.TVSeriesSortBy;
import com.ness.movie_release_web.repository.UserTVSeriesRepository;
import com.ness.movie_release_web.service.tmdb.TmdbTVSeriesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class TVSeriesServiceImpl implements TVSeriesService {

    @Autowired
    private UserTVSeriesRepository userTVSeriesRepository;

    @Autowired
    private TVSeriesRepository tvSeriesRepository;

    @Autowired
    private TmdbTVSeriesService tvSeriesService;


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

        Optional<TVDetailsDto> tvDetailsOptional = tvSeriesService.getTVDetails(tmdbId, Language.en);
        Optional<TVDetailsDto> tvDetailsOptionalRu = tvSeriesService.getTVDetails(tmdbId, Language.ru);

        if (!tvDetailsOptional.isPresent() || !tvDetailsOptionalRu.isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT);

        TVDetailsDto tvDetails = tvDetailsOptional.get();
        TVDetailsDto tvDetailsRu = tvDetailsOptionalRu.get();

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

        Optional<TVDetailsDto> tvDetailsOptional = tvSeriesService.getTVDetails(tmdbId, user.getLanguage());
        if (!tvDetailsOptional.isPresent())
            return 0L;

        TVDetailsDto tvDetails = tvDetailsOptional.get();
        Double average = tvDetails.getEpisodeRunTime().stream().mapToDouble(Long::doubleValue).average().orElse(0d);
        Double result = 0d;


        for (int i = 1; i < currentSeason; i++) {
            Optional<SeasonDto> seasonDetailsOpt = tvSeriesService.getSeasonDetails(tmdbId, (long) i, user.getLanguage());
            if (!seasonDetailsOpt.isPresent())
                continue;

            SeasonDto seasonDto = seasonDetailsOpt.get();
            result += seasonDto.getEpisodes().size() * average;
        }

        Optional<SeasonDto> seasonDetails = tvSeriesService.getSeasonDetails(tmdbId, currentSeason, user.getLanguage());

        if (!seasonDetails.isPresent()) {
            return result.longValue();
        }
        SeasonDto seasonDto = seasonDetails.get();

        result += seasonDto.getEpisodes().stream().filter(e -> e.getEpisodeNumber() <= currentEpisode).count() * average;

        return result.longValue();
    }

    @Override
    public Long spentTotalMinutesToSeriesSeason(Long tmdbId, Long season, User user, Long currentSeason, Long currentEpisode) {

        Optional<TVDetailsDto> tvDetailsOptional = tvSeriesService.getTVDetails(tmdbId, user.getLanguage());
        if (!tvDetailsOptional.isPresent())
            return 0L;

        TVDetailsDto tvDetails = tvDetailsOptional.get();
        double average = tvDetails.getEpisodeRunTime().stream().mapToDouble(Long::doubleValue).average().orElse(0d);

        Optional<SeasonDto> seasonDetails = tvSeriesService.getSeasonDetails(tmdbId, currentSeason, user.getLanguage());

        if (season < currentSeason)
            return ((Double) (seasonDetails.orElse(new SeasonDto()).getEpisodes().size() * average)).longValue();

        if (season.equals(currentSeason))
            return ((Double) (seasonDetails.orElse(new SeasonDto()).getEpisodes().stream().filter(e -> e.getEpisodeNumber() <= currentEpisode).count() * average)).longValue();

        return 0L;
    }

    @Scheduled(cron = "${cron.pattern.updateDB}")
    @Override
    public void updateDB() {

        log.info("Updating series db...");
        tvSeriesRepository.findAll().forEach(tvs -> {
            Long id = tvs.getId();
            Optional<TVDetailsDto> tvDetails = tvSeriesService.getTVDetails(id, Language.en);

            if (!tvDetails.isPresent()) {
                return;
            }

            TVDetailsDto tvDetailsDto = tvDetails.get();
            tvs.setReleaseDate(tvDetailsDto.getFirstAirDate());
            tvs.setLastEpisodeAirDate(tvDetailsDto.getLastAirDate());
            tvs.setVoteAverage(tvDetailsDto.getVoteAverage());
            tvs.setNameEn(tvDetailsDto.getName());
            tvs.setStatus(tvDetailsDto.getStatus());

            Optional<TVDetailsDto> tvDetailsRu = tvSeriesService.getTVDetails(id, Language.ru);
            tvDetailsRu.ifPresent(tvDetailsDto1 -> tvs.setNameRu(tvDetailsDto1.getName()));

            Optional<SeasonDto> seasonDetails = tvSeriesService.getSeasonDetails(id, tvDetailsDto.getNumberOfSeasons(), Language.en);
            if (!seasonDetails.isPresent()) {
                tvSeriesRepository.save(tvs);
                return;
            }

            SeasonDto seasonDto = seasonDetails.get();
            seasonDto.getEpisodes().stream().min((s1, s2) -> ((Long) (s2.getEpisodeNumber() - s1.getEpisodeNumber())).intValue())
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
