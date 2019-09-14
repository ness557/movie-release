package com.ness.movie_release_web.service;

import com.ness.movie_release_web.dto.Language;
import com.ness.movie_release_web.dto.Mode;
import com.ness.movie_release_web.dto.TimeSpentDto;
import com.ness.movie_release_web.dto.tmdb.movie.discover.TmdbDiscoverSearchCriteria;
import com.ness.movie_release_web.dto.tmdb.tvSeries.WatchStatus;
import com.ness.movie_release_web.dto.tmdb.tvSeries.details.Status;
import com.ness.movie_release_web.dto.tmdb.tvSeries.details.TmdbEpisodeDto;
import com.ness.movie_release_web.dto.tmdb.tvSeries.details.TmdbSeasonDto;
import com.ness.movie_release_web.dto.tmdb.tvSeries.details.TmdbTVDetailsDto;
import com.ness.movie_release_web.dto.tmdb.tvSeries.search.TmdbTVDto;
import com.ness.movie_release_web.dto.tmdb.tvSeries.search.TmdbTVSearchDto;
import com.ness.movie_release_web.dto.tvseries.*;
import com.ness.movie_release_web.model.TVSeries;
import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.model.UserTVSeries;
import com.ness.movie_release_web.model.UserTVSeriesPK;
import com.ness.movie_release_web.repository.TVSeriesRepository;
import com.ness.movie_release_web.repository.TVSeriesSortBy;
import com.ness.movie_release_web.repository.UserRepository;
import com.ness.movie_release_web.repository.UserTVSeriesRepository;
import com.ness.movie_release_web.service.tmdb.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.ness.movie_release_web.model.type.MessageDestinationType.EMAIL;
import static com.ness.movie_release_web.repository.UserTVSeriesSpecifications.byUserAndTVStatusesAndWatchStatusesWithOrderby;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class TVSeriesServiceImpl implements TVSeriesService {

    private final UserTVSeriesRepository userTVSeriesRepository;
    private final TVSeriesRepository tvSeriesRepository;
    private final TmdbTVSeriesService tmdbSeriesService;
    private final UserRepository userRepository;
    private final TmdbDiscoverService discoverService;
    private final TmdbCompanyService tmdbCompanyService;
    private final TmdbNetworkService tmdbNetworkService;
    private final TmdbGenreService tmdbGenreService;

    public TvSeriesDto getShow(Long tmdbId, Language language, Mode mode, String login) {
        TvSeriesDto result = new TvSeriesDto();
        User user = userRepository.findByLogin(login);

        TmdbTVDetailsDto tmdbTVDetails = tmdbSeriesService.getTVDetails(tmdbId, language)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        result.setTvDetailsDto(tmdbTVDetails);

        getByTmdbIdAndUserId(tmdbId, user.getId()).ifPresent(userTVSeries -> {
            result.setSubscribed(true);

            Long currentSeasonNum = userTVSeries.getCurrentSeason();
            Long currentEpisodeNum = userTVSeries.getCurrentEpisode();
            TmdbEpisodeDto lastEpisodeToAir = tmdbTVDetails.getLastEpisodeToAir();

            result.setCurrentSeason(currentSeasonNum);

            if (currentSeasonNum > 0) {
                TmdbSeasonDto tmdbSeason = tmdbSeriesService.getSeasonDetails(tmdbId, currentSeasonNum, language)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

                result.setSeasonWatched(tmdbSeason.getEpisodes().stream().noneMatch(e -> e.getEpisodeNumber() > currentEpisodeNum));
            }

            if (lastEpisodeToAir != null) {
                result.setLastEpisodeWatched(lastEpisodeToAir.getSeasonNumber() < currentSeasonNum
                        || (lastEpisodeToAir.getSeasonNumber().equals(currentSeasonNum) && lastEpisodeToAir.getEpisodeNumber() <= currentEpisodeNum));
            }

            Long minutes = spentTotalMinutesToSeries(tmdbId, user, currentSeasonNum, currentEpisodeNum);
            fillTimeSpent(result, minutes);

        });

        return result;
    }


    public TvSeriesSeasonDto getSeason(Long tmdbId, Long seasonNumber, Language language, String login) {

        User user = userRepository.findByLogin(login);

        TvSeriesSeasonDto result = new TvSeriesSeasonDto();

        TmdbTVDetailsDto tvDetails = tmdbSeriesService.getTVDetails(tmdbId, language)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        TmdbSeasonDto seasonDetails = tmdbSeriesService.getSeasonDetails(tmdbId, seasonNumber, language)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        result.setTvDetailsDto(tvDetails)
                .setSeasonDto(seasonDetails);

        getByTmdbIdAndUserId(tmdbId, user.getId()).ifPresent(userTVSeries -> {
            Long currentSeason = userTVSeries.getCurrentSeason();
            Long currentEpisode = userTVSeries.getCurrentEpisode();

            result.setSubscribed(true)
                    .setCurrentSeason(currentSeason)
                    .setCurrentEpisode(currentEpisode);

            Long minutes = spentTotalMinutesToSeriesSeason(tmdbId, seasonNumber, user, currentSeason, currentEpisode);
            fillTimeSpent(result, minutes);

        });

        return result;
    }

    public TvSeriesSearchDto search(String query, Long year, Long page, Language language, String login) {

        TvSeriesSearchDto result = new TvSeriesSearchDto();

        User user = userRepository.findByLogin(login);

        TmdbTVSearchDto tmdbTVSearchDto = tmdbSeriesService.search(query, page.intValue(), year, language)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));

        return result.setSeriesSearchDtoMap(getDtoMap(tmdbTVSearchDto, user))
                .setTotal(tmdbTVSearchDto.getTotalPages());
    }

    public TvSeriesSubscriptionsDto getSubscriptions(List<Status> statuses,
                                                     List<WatchStatus> watchStatuses,
                                                     TVSeriesSortBy sort,
                                                     Boolean viewMode,
                                                     Long page,
                                                     String login,
                                                     Language language) {


        if (sort == null) {
            switch (language) {
                case en:
                    sort = TVSeriesSortBy.NameEn_asc;
                    break;
                case ru:
                    sort = TVSeriesSortBy.NameRu_asc;
                    break;
            }
        }

        int size = viewMode ? 30 : 10;

        User user = userRepository.findByLogin(login);
        Page<UserTVSeries> userTVSeries = getByUserAndTVStatusesAndWatchStatusesWithOrderAndPages(statuses, watchStatuses, sort, user, (int) (page - 1), size);
        List<UserTVSeries> series = userTVSeries.getContent();

        List<TmdbTVDetailsDto> subscriptions;
        if (viewMode) {
            subscriptions = series.stream()
                    .map(UserTVSeries::getTvSeries)
                    .map(s -> TmdbTVDetailsDto.of(s, language)).collect(toList());
        } else {
            subscriptions = series.stream()
                    .map(f -> tmdbSeriesService.getTVDetails(f.getId().getTvSeriesId(), language))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(toList());
        }

        return new TvSeriesSubscriptionsDto()
                .setSortBy(sort)
                .setBotInitialized(user.getMessageDestinationType().equals(EMAIL) || user.getTelegramChatId() != null)
                .setSeriesDetailsDtos(subscriptions)
                .setTotalPages((long) userTVSeries.getTotalPages());
    }

    public TvSeriesDiscoverDto discover(TmdbDiscoverSearchCriteria criteria, String login) {

        User user = userRepository.findByLogin(login);
        TvSeriesDiscoverDto result = new TvSeriesDiscoverDto();

        discoverService.discoverSeries(criteria)
                .ifPresent(tmdbTVSearchDto ->
                        result.setSeriesSearchDtoMap(getDtoMap(tmdbTVSearchDto, user))
                                .setTotalPages(tmdbTVSearchDto.getTotalPages() > 1000 ? 1000 : tmdbTVSearchDto.getTotalPages()));

        return result.setCompanies(tmdbCompanyService.getCompanies(criteria.getCompanies(), criteria.getLanguage()))
                .setNetworks(tmdbNetworkService.getNetworks(criteria.getNetworks()))
                .setGenres(tmdbGenreService.getTVGenres(criteria.getLanguage()));
    }


    private Boolean isExistsByTmdbIdAndUserId(Long tmdbId, Long userId) {
        return userTVSeriesRepository.existsById(UserTVSeriesPK.wrap(userId, tmdbId));
    }

    private Optional<UserTVSeries> getByTmdbIdAndUserId(Long tmdbId, Long userId) {
        return userTVSeriesRepository.findById(UserTVSeriesPK.wrap(userId, tmdbId));
    }

    @Override
    public void setSeasonAndEpisode(Long tmdbId, User user, Long seasonNum, Long episodeNum) {

        Optional<TmdbTVDetailsDto> tvDetailsOptional = tmdbSeriesService.getTVDetails(tmdbId, Language.en);
        Optional<TmdbTVDetailsDto> tvDetailsOptionalRu = tmdbSeriesService.getTVDetails(tmdbId, Language.ru);

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

    private Page<UserTVSeries> getByUserAndTVStatusesAndWatchStatusesWithOrderAndPages(
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

    private Long spentTotalMinutesToSeries(Long tmdbId, User user, Long currentSeason, Long currentEpisode) {

        Optional<TmdbTVDetailsDto> tvDetailsOptional = tmdbSeriesService.getTVDetails(tmdbId, user.getLanguage());
        if (!tvDetailsOptional.isPresent())
            return 0L;

        TmdbTVDetailsDto tvDetails = tvDetailsOptional.get();
        Double average = tvDetails.getEpisodeRunTime().stream().mapToDouble(Long::doubleValue).average().orElse(0d);
        Double result = 0d;


        for (int i = 1; i < currentSeason; i++) {
            Optional<TmdbSeasonDto> seasonDetailsOpt = tmdbSeriesService.getSeasonDetails(tmdbId, (long) i, user.getLanguage());
            if (!seasonDetailsOpt.isPresent())
                continue;

            TmdbSeasonDto tmdbSeasonDto = seasonDetailsOpt.get();
            result += tmdbSeasonDto.getEpisodes().size() * average;
        }

        Optional<TmdbSeasonDto> seasonDetails = tmdbSeriesService.getSeasonDetails(tmdbId, currentSeason, user.getLanguage());

        if (!seasonDetails.isPresent()) {
            return result.longValue();
        }
        TmdbSeasonDto tmdbSeasonDto = seasonDetails.get();

        result += tmdbSeasonDto.getEpisodes().stream().filter(e -> e.getEpisodeNumber() <= currentEpisode).count() * average;

        return result.longValue();
    }

    private void fillTimeSpent(TimeSpentDto dto, Long minutes) {
        if (minutes > 60) {
            dto.setHoursSpent(TimeUnit.MINUTES.toHours(minutes));
            minutes %= 60;
        }

        dto.setMinutesSpent(minutes);
    }

    private Map<TmdbTVDto, Boolean> getDtoMap(TmdbTVSearchDto searchDto, User user) {
        return searchDto.getResults().stream()
                .collect(toMap(
                        f -> f,
                        f -> isExistsByTmdbIdAndUserId(f.getId(), user.getId()),
                        (f1, f2) -> f1,
                        LinkedHashMap::new));
    }

    private Long spentTotalMinutesToSeriesSeason(Long tmdbId, Long season, User user, Long currentSeason, Long currentEpisode) {

        Optional<TmdbTVDetailsDto> tvDetailsOptional = tmdbSeriesService.getTVDetails(tmdbId, user.getLanguage());
        if (!tvDetailsOptional.isPresent())
            return 0L;

        TmdbTVDetailsDto tvDetails = tvDetailsOptional.get();
        double average = tvDetails.getEpisodeRunTime().stream().mapToDouble(Long::doubleValue).average().orElse(0d);

        Optional<TmdbSeasonDto> seasonDetails = tmdbSeriesService.getSeasonDetails(tmdbId, currentSeason, user.getLanguage());

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
            Optional<TmdbTVDetailsDto> tvDetails = tmdbSeriesService.getTVDetails(id, Language.en);

            if (!tvDetails.isPresent()) {
                log.warn("No such tv series at TMDB: {}", tvs);
                return;
            }

            TmdbTVDetailsDto tmdbTvDetailsDto = tvDetails.get();
            tvs.setReleaseDate(tmdbTvDetailsDto.getFirstAirDate());
            tvs.setLastEpisodeAirDate(tmdbTvDetailsDto.getLastAirDate());
            tvs.setVoteAverage(tmdbTvDetailsDto.getVoteAverage());
            tvs.setNameEn(tmdbTvDetailsDto.getName());
            tvs.setStatus(tmdbTvDetailsDto.getStatus());

            Optional<TmdbTVDetailsDto> tvDetailsRu = tmdbSeriesService.getTVDetails(id, Language.ru);
            tvDetailsRu.ifPresent(tvDetailsDto1 -> tvs.setNameRu(tvDetailsDto1.getName()));

            Optional<TmdbSeasonDto> seasonDetails = tmdbSeriesService.getSeasonDetails(id, tmdbTvDetailsDto.getNumberOfSeasons(), Language.en);
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
}
