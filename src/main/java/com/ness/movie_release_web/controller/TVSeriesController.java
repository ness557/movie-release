package com.ness.movie_release_web.controller;

import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.model.UserTVSeries;
import com.ness.movie_release_web.dto.Language;
import com.ness.movie_release_web.dto.Mode;
import com.ness.movie_release_web.dto.tmdb.movie.discover.TmdbDiscoverSearchCriteria;
import com.ness.movie_release_web.dto.tmdb.tvSeries.WatchStatus;
import com.ness.movie_release_web.dto.tmdb.tvSeries.details.TmdbEpisodeDto;
import com.ness.movie_release_web.dto.tmdb.tvSeries.details.TmdbSeasonDto;
import com.ness.movie_release_web.dto.tmdb.tvSeries.details.Status;
import com.ness.movie_release_web.dto.tmdb.tvSeries.details.TmdbTVDetailsDto;
import com.ness.movie_release_web.dto.tmdb.tvSeries.search.TmdbTVSearchDto;
import com.ness.movie_release_web.dto.tmdb.tvSeries.search.TmdbTVDto;
import com.ness.movie_release_web.repository.TVSeriesSortBy;
import com.ness.movie_release_web.service.SubscriptionService;
import com.ness.movie_release_web.service.TVSeriesService;
import com.ness.movie_release_web.service.UserService;
import com.ness.movie_release_web.service.tmdb.*;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Controller
@RequestMapping("/series")
@SessionAttributes(names = {"query", "year", "language", "mode"},
        types = {String.class, Long.class, Language.class, Mode.class})
@AllArgsConstructor
public class TVSeriesController {

    private UserService userService;
    private TVSeriesService dbSeriesService;
    private TmdbTVSeriesService tmdbSeriesService;
    private TmdbDiscoverService tmdbDiscoverService;
    private TmdbCompanyService tmdbCompanyService;
    private TmdbGenreService tmdbGenreService;
    private TmdbNetworkService tmdbNetworkService;
    private SubscriptionService subscriptionService;

    @GetMapping("/{tmdbId}")
    public String getFilm(@PathVariable("tmdbId") Long tmdbId,
                          @CookieValue(value = "language", defaultValue = "en") Language language,
                          @CookieValue(value = "mode", defaultValue = "movie") Mode mode,
                          Principal principal,
                          Model model) {

        User user = userService.findByLogin(principal.getName());
        model.addAttribute("language", language);
        model.addAttribute("mode", mode);
        model.addAttribute("subscribed", false);

        Optional<TmdbTVDetailsDto> tvDetails = tmdbSeriesService.getTVDetails(tmdbId, language);
        if (!tvDetails.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        TmdbTVDetailsDto tmdbTvDetailsDto = tvDetails.get();

        model.addAttribute("series", tmdbTvDetailsDto);

        Optional<UserTVSeries> userTVSeriesOptional = dbSeriesService.getByTmdbIdAndUserId(tmdbId, user.getId());
        if (userTVSeriesOptional.isPresent()) {
            UserTVSeries userTVSeries = userTVSeriesOptional.get();
            model.addAttribute("subscribed", true);

            Long currentSeasonNum = userTVSeries.getCurrentSeason();
            Long currentEpisodeNum = userTVSeries.getCurrentEpisode();
            TmdbEpisodeDto lastEpisodeToAir = tmdbTvDetailsDto.getLastEpisodeToAir();

            model.addAttribute("currentSeason", currentSeasonNum);
            model.addAttribute("seasonWatched", false);

            if (currentSeasonNum > 0) {
                Optional<TmdbSeasonDto> seasonDetailsOpt = tmdbSeriesService.getSeasonDetails(tmdbId, currentSeasonNum, language);
                if (!seasonDetailsOpt.isPresent())
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND);


                TmdbSeasonDto tmdbSeasonDto = seasonDetailsOpt.get();

                model.addAttribute("seasonWatched", tmdbSeasonDto.getEpisodes().stream().noneMatch(e -> e.getEpisodeNumber() > currentEpisodeNum));
            }

            model.addAttribute("lastEpisodeWatched", false);

            if (lastEpisodeToAir != null) {
                model.addAttribute("lastEpisodeWatched",
                        lastEpisodeToAir.getSeasonNumber() < currentSeasonNum ||
                                (lastEpisodeToAir.getSeasonNumber().equals(currentSeasonNum) && lastEpisodeToAir.getEpisodeNumber() <= currentEpisodeNum));
            }

            Long minutes = dbSeriesService.spentTotalMinutesToSeries(tmdbId, user, userTVSeries.getCurrentSeason(), userTVSeries.getCurrentEpisode());
            if (minutes > 60) {
                model.addAttribute("hours", TimeUnit.MINUTES.toHours(minutes));
                minutes = minutes % 60;
            }

            model.addAttribute("minutes", minutes);
        }

        return "seriesInfo";
    }

    @GetMapping("/{tmdbId}/season/{seasonNumber}")
    public String getSeason(@PathVariable("tmdbId") Long tmdbId,
                            @PathVariable("seasonNumber") Long seasonNumber,
                            @RequestParam(value = "episodeToOpen", required = false, defaultValue = "0") Long episodeToOpen,
                            @CookieValue(value = "language", defaultValue = "en") Language language,
                            @CookieValue(value = "mode", defaultValue = "movie") Mode mode,
                            Principal principal,
                            Model model) {

        User user = userService.findByLogin(principal.getName());
        model.addAttribute("language", language);
        model.addAttribute("mode", mode);

//        default values
        model.addAttribute("subscribed", false);
        model.addAttribute("episodeToOpen", episodeToOpen);


        Optional<TmdbTVDetailsDto> series = tmdbSeriesService.getTVDetails(tmdbId, language);
        if (!series.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        Optional<TmdbSeasonDto> season = tmdbSeriesService.getSeasonDetails(tmdbId, seasonNumber, language);
        if (!season.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        TmdbTVDetailsDto seriesDto = series.get();
        TmdbSeasonDto tmdbSeasonDto = season.get();

        model.addAttribute("series", seriesDto);
        model.addAttribute("season", tmdbSeasonDto);

        Optional<UserTVSeries> userTVSeriesOptional = dbSeriesService.getByTmdbIdAndUserId(tmdbId, user.getId());
        if (userTVSeriesOptional.isPresent()) {
            UserTVSeries userTVSeries = userTVSeriesOptional.get();
            model.addAttribute("subscribed", true);


            model.addAttribute("currentSeason", userTVSeries.getCurrentSeason());
            model.addAttribute("currentEpisode", userTVSeries.getCurrentEpisode());

            Long minutes = dbSeriesService.spentTotalMinutesToSeriesSeason(tmdbId, seasonNumber, user, userTVSeries.getCurrentSeason(), userTVSeries.getCurrentEpisode());
            if (minutes > 60) {
                model.addAttribute("hours", TimeUnit.MINUTES.toHours(minutes));
                minutes = minutes % 60;
            }

            model.addAttribute("minutes", minutes);
        }

        return "seasonInfo";
    }

    @PostMapping("/subscribe")
    @ResponseStatus(value = HttpStatus.OK)
    public void subscribe(@RequestParam(value = "tmdbId") Long tmdbId,
                          Principal principal) {
        subscriptionService.subscribeToSeries(tmdbId, principal.getName());
    }

    @PostMapping("/unSubscribe")
    @ResponseStatus(value = HttpStatus.OK)
    public void unSubscribe(@RequestParam(value = "tmdbId") Long tmdbId,
                            Principal principal) {
        subscriptionService.unsubscribeFromSeries(tmdbId, principal.getName());
    }

    @GetMapping("/search")
    public String search(@RequestParam("query") String query,
                         @RequestParam(required = false, name = "year") Long year,
                         @RequestParam(required = false, name = "page") Integer page,
                         @CookieValue(value = "language", defaultValue = "en") Language language,
                         @CookieValue(value = "mode", defaultValue = "movie") Mode mode,
                         Principal principal,
                         Model model) {

        User user = userService.findByLogin(principal.getName());

        // trim space at start
        query = StringUtils.trim(query);

        //save in session
        model.addAttribute("query", query);
        model.addAttribute("year", year);
        model.addAttribute("language", language);
        model.addAttribute("mode", mode);

        Optional<TmdbTVSearchDto> optionalSearchResult = tmdbSeriesService.search(query, page, year, language);

        if (!optionalSearchResult.isPresent()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        TmdbTVSearchDto searchDto = optionalSearchResult.get();

        Map<TmdbTVDto, Boolean> filmsWithSubFlags = getDtoMap(searchDto, user);

        model.addAttribute("series", filmsWithSubFlags);

        model.addAttribute("pageCount", searchDto.getTotalPages());
        model.addAttribute("page", page);

        return "seriesSearchResult";
    }

    @GetMapping("/api/search")
    @ResponseBody
    public ResponseEntity<TmdbTVSearchDto> searchApi(@RequestParam("query") String query,
                                                     @RequestParam(required = false, name = "year") Long year,
                                                     @RequestParam(required = false, name = "page") Integer page,
                                                     @CookieValue(value = "language", defaultValue = "en") Language language) {

        Optional<TmdbTVSearchDto> optionalSearchResult = tmdbSeriesService.search(query, page, year, language);
        if (!optionalSearchResult.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }

        return ResponseEntity.ok(optionalSearchResult.get());
    }

    @GetMapping("/subscriptions")
    public String getSubs(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                          @RequestParam(value = "statuses", required = false, defaultValue = "") List<Status> tvStatuses,
                          @RequestParam(value = "sortBy", required = false) TVSeriesSortBy sortBy,
                          @RequestParam(value = "watch_status", required = false, defaultValue = "") List<WatchStatus> watchStatuses,
                          @CookieValue(value = "viewMode", required = false, defaultValue = "false") Boolean viewMode,
                          @CookieValue(value = "language", defaultValue = "en") Language language,
                          @CookieValue(value = "mode", defaultValue = "movie") Mode mode,
                          Principal principal,
                          Model model) {

        User user = userService.findByLogin(principal.getName());

        if (sortBy == null) {
            switch (language) {
                case en:
                    sortBy = TVSeriesSortBy.NameEn_asc;
                    break;
                case ru:
                    sortBy = TVSeriesSortBy.NameRu_asc;
                    break;
            }
        }

        //save in session
        model.addAttribute("language", language);
        model.addAttribute("mode", mode);

        int size = viewMode ? 30 : 10;

        Page<UserTVSeries> userTVSeries = dbSeriesService.getByUserAndTVStatusesAndWatchStatusesWithOrderAndPages(tvStatuses, watchStatuses, sortBy, user, page - 1, size);
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

        model.addAttribute("statuses", tvStatuses);
        model.addAttribute("watch_statuses", watchStatuses);
        model.addAttribute("sortBy", sortBy);

        model.addAttribute("botInitialized", !user.isTelegramNotify() || user.getTelegramChatId() != null);

        model.addAttribute("series", subscriptions)
                .addAttribute("page", page)
                .addAttribute("pageCount", userTVSeries.getTotalPages());

        model.addAttribute("asList", viewMode);

        return "seriesSubscriptions";
    }

    @GetMapping("/discover")
    public String discover(@ModelAttribute("criteria") TmdbDiscoverSearchCriteria criteria,
                           @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                           @CookieValue(value = "language", defaultValue = "en") Language language,
                           @CookieValue(value = "mode", defaultValue = "movie") Mode mode,
                           Principal principal,
                           Model model) {

        User user = userService.findByLogin(principal.getName());

        // assigning values for service
        criteria.setPage(page);
        criteria.setLanguage(language);

        Optional<TmdbTVSearchDto> optionalMovieSearch = tmdbDiscoverService.discoverSeries(criteria);

        if (optionalMovieSearch.isPresent()) {

            TmdbTVSearchDto movieSearchDto = optionalMovieSearch.get();

            Map<TmdbTVDto, Boolean> filmsWithSubFlags = getDtoMap(movieSearchDto, user);
            model.addAttribute("series", filmsWithSubFlags);

            model.addAttribute("pageCount", movieSearchDto.getTotalPages() > 1000 ? 1000 : movieSearchDto.getTotalPages());
            model.addAttribute("page", page);
        }

        model.addAttribute("language", language);
        model.addAttribute("mode", mode);

        // adding all attributes to form
        model.addAttribute("companies", tmdbCompanyService.getCompanies(criteria.getCompanies(), language));
        model.addAttribute("networks", tmdbNetworkService.getNetworks(criteria.getNetworks()));
        model.addAttribute("criteria", criteria);

        // adding genres to form
        model.addAttribute("genres", tmdbGenreService.getTVGenres(language));

        return "discoverSeries";
    }

    @PostMapping("/setSeasonAndEpisode")
    @ResponseStatus(value = HttpStatus.OK)
    public void setCurrentSeasonAndEpisode(@RequestParam(value = "tmdbId") Long tmdbId,
                                           @RequestParam(value = "season") Long season,
                                           @RequestParam(value = "episode") Long episode,
                                           Principal principal) {

        User user = userService.findByLogin(principal.getName());

        dbSeriesService.setSeasonAndEpisode(tmdbId, user, season, episode);
    }

    private LinkedHashMap<TmdbTVDto, Boolean> getDtoMap(TmdbTVSearchDto searchDto, User user) {
        return searchDto.getResults().stream()
                .collect(toMap(
                        f -> f,
                        f -> dbSeriesService.isExistsByTmdbIdAndUserId(f.getId(), user.getId()),
                        (f1, f2) -> f1,
                        LinkedHashMap::new));
    }
}
