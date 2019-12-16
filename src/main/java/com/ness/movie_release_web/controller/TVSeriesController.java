package com.ness.movie_release_web.controller;

import com.ness.movie_release_web.dto.*;
import com.ness.movie_release_web.dto.tvseries.*;
import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.dto.tmdb.movie.discover.TmdbDiscoverSearchCriteria;
import com.ness.movie_release_web.dto.tmdb.tvSeries.WatchStatus;
import com.ness.movie_release_web.dto.tmdb.tvSeries.details.Status;
import com.ness.movie_release_web.dto.tmdb.tvSeries.search.TmdbTVSearchDto;
import com.ness.movie_release_web.dto.tmdb.tvSeries.search.TmdbTVDto;
import com.ness.movie_release_web.repository.TVSeriesSortBy;
import com.ness.movie_release_web.service.SubscriptionService;
import com.ness.movie_release_web.service.TVSeriesService;
import com.ness.movie_release_web.service.UserService;
import com.ness.movie_release_web.service.tmdb.*;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;

@Controller
@RequestMapping("/series")
@RequiredArgsConstructor
public class TVSeriesController {

    private final UserService userService;
    private final TVSeriesService tvSeriesService;
    private final TmdbTVSeriesService tmdbSeriesService;
    private final SubscriptionService subscriptionService;

    @GetMapping("/{tmdbId}")
    public String getShow(@PathVariable("tmdbId") Long tmdbId,
                          @CookieValue(value = "language", defaultValue = "en") Language language,
                          @CookieValue(value = "mode", defaultValue = "movie") Mode mode,
                          Principal principal,
                          Model model) {

        model.addAttribute("language", language);
        model.addAttribute("mode", mode);

        TvSeriesDto show = tvSeriesService.getShow(tmdbId, language, mode, principal.getName());
        model.addAttribute("subscribed", show.isSubscribed());
        model.addAttribute("series", show.getTvDetailsDto());
        model.addAttribute("currentSeason", show.getCurrentSeason());
        model.addAttribute("seasonWatched", show.isSeasonWatched());
        model.addAttribute("lastEpisodeWatched", show.isLastEpisodeWatched());
        model.addAttribute("hours", show.getHoursSpent());
        model.addAttribute("minutes", show.getMinutesSpent());

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

        TvSeriesSeasonDto season = tvSeriesService.getSeason(tmdbId, seasonNumber, language, principal.getName());

        model.addAttribute("language", language);
        model.addAttribute("mode", mode);
        model.addAttribute("episodeToOpen", episodeToOpen);

        model.addAttribute("subscribed", season.isSubscribed());
        model.addAttribute("series", season.getTvDetailsDto());
        model.addAttribute("season", season.getSeasonDto());
        model.addAttribute("currentSeason", season.getCurrentSeason());
        model.addAttribute("currentEpisode", season.getCurrentEpisode());
        model.addAttribute("hours", season.getHoursSpent());
        model.addAttribute("minutes", season.getMinutesSpent());

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
                         @RequestParam(required = false, name = "page", defaultValue = "1") Long page,
                         @CookieValue(value = "language", defaultValue = "en") Language language,
                         @CookieValue(value = "mode", defaultValue = "movie") Mode mode,
                         Principal principal,
                         Model model) {

        // trim space at start
        query = StringUtils.trim(query);
        TvSeriesSearchDto searchResult = tvSeriesService.search(query, year, page, language, principal.getName());

        model.addAttribute("query", query);
        model.addAttribute("language", language);
        model.addAttribute("year", year);
        model.addAttribute("mode", mode);
        model.addAttribute("series", searchResult.getSeriesSearchDtoMap());
        model.addAttribute("pageCount", searchResult.getTotal());
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
    public String getSubs(@RequestParam(value = "page", required = false, defaultValue = "1") Long page,
                          @RequestParam(value = "statuses", required = false, defaultValue = "") List<Status> tvStatuses,
                          @RequestParam(value = "sortBy", required = false) TVSeriesSortBy sortBy,
                          @RequestParam(value = "watch_status", required = false, defaultValue = "") List<WatchStatus> watchStatuses,
                          @CookieValue(value = "viewMode", required = false, defaultValue = "false") Boolean viewMode,
                          @CookieValue(value = "language", defaultValue = "en") Language language,
                          @CookieValue(value = "mode", defaultValue = "movie") Mode mode,
                          Principal principal,
                          Model model) {

        TvSeriesSubscriptionsDto subscriptionsDto = tvSeriesService.getSubscriptions(tvStatuses, watchStatuses, sortBy, viewMode, page, principal.getName(), language);

        model.addAttribute("language", language);
        model.addAttribute("mode", mode);
        model.addAttribute("statuses", tvStatuses);
        model.addAttribute("watch_statuses", watchStatuses);
        model.addAttribute("sortBy", subscriptionsDto.getSortBy());
        model.addAttribute("botInitialized", subscriptionsDto.getBotInitialized());
        model.addAttribute("series", subscriptionsDto.getSeriesDetailsDtos());
        model.addAttribute("page", page);
        model.addAttribute("pageCount", subscriptionsDto.getTotalPages());
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


        // assigning values for service
        criteria.setPage(page);
        criteria.setLanguage(language);

        TvSeriesDiscoverDto discoverResult = tvSeriesService.discover(criteria, principal.getName());

        model.addAttribute("series", discoverResult.getSeriesSearchDtoMap());
        model.addAttribute("pageCount", discoverResult.getTotalPages());
        model.addAttribute("page", page);
        model.addAttribute("language", language);
        model.addAttribute("mode", mode);
        model.addAttribute("companies", discoverResult.getCompanies());
        model.addAttribute("networks", discoverResult.getNetworks());
        model.addAttribute("criteria", criteria);
        model.addAttribute("genres", discoverResult.getGenres());

        return "discoverSeries";
    }

    @PostMapping("/setSeasonAndEpisode")
    @ResponseStatus(value = HttpStatus.OK)
    public void setCurrentSeasonAndEpisode(@RequestParam(value = "tmdbId") Long tmdbId,
                                           @RequestParam(value = "season") Long season,
                                           @RequestParam(value = "episode") Long episode,
                                           Principal principal) {

        User user = userService.findByLogin(principal.getName());

        tvSeriesService.setSeasonAndEpisode(tmdbId, user, season, episode);
    }
}
