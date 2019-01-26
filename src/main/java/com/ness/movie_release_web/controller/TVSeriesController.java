package com.ness.movie_release_web.controller;

import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.model.UserTVSeries;
import com.ness.movie_release_web.model.wrapper.tmdb.Language;
import com.ness.movie_release_web.model.wrapper.tmdb.Mode;
import com.ness.movie_release_web.model.wrapper.tmdb.movie.discover.DiscoverSearchCriteria;
import com.ness.movie_release_web.model.wrapper.tmdb.tvSeries.WatchStatus;
import com.ness.movie_release_web.model.wrapper.tmdb.tvSeries.details.EpisodeWrapper;
import com.ness.movie_release_web.model.wrapper.tmdb.tvSeries.details.SeasonWrapper;
import com.ness.movie_release_web.model.wrapper.tmdb.tvSeries.details.Status;
import com.ness.movie_release_web.model.wrapper.tmdb.tvSeries.details.TVDetailsWrapper;
import com.ness.movie_release_web.model.wrapper.tmdb.tvSeries.search.TVSearchWrapper;
import com.ness.movie_release_web.model.wrapper.tmdb.tvSeries.search.TVWrapper;
import com.ness.movie_release_web.repository.TVSeriesSortBy;
import com.ness.movie_release_web.service.TVSeriesService;
import com.ness.movie_release_web.service.UserService;
import com.ness.movie_release_web.service.tmdb.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Controller
@RequestMapping("/series")
@SessionAttributes(names = {"query", "year", "language", "mode"},
        types = {String.class, Integer.class, Language.class, Mode.class})
public class TVSeriesController {

    @Autowired
    private UserService userService;

    @Autowired
    private TVSeriesService dbSeriesService;

    @Autowired
    private TmdbTVSeriesService tmdbSeriesService;

    @Autowired
    private DiscoverService discoverService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private GenreService genreService;

    @Autowired
    private NetworkService networkService;

    @GetMapping("/{tmdbId}")
    public String getFilm(@PathVariable("tmdbId") Integer tmdbId,
                          @CookieValue(value = "language", defaultValue = "en") Language language,
                          @CookieValue(value = "mode", defaultValue = "movie") Mode mode,
                          Principal principal,
                          Model model) {

        User user = userService.findByLogin(principal.getName());
        model.addAttribute("language", language);
        model.addAttribute("mode", mode);
        model.addAttribute("subscribed", false);

        Optional<TVDetailsWrapper> tvDetails = tmdbSeriesService.getTVDetails(tmdbId, language);
        if (!tvDetails.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        TVDetailsWrapper tvDetailsWrapper = tvDetails.get();

        model.addAttribute("series", tvDetailsWrapper);

        Optional<UserTVSeries> userTVSeriesOptional = dbSeriesService.getByTmdbIdAndUserId(tmdbId, user.getId());
        if (userTVSeriesOptional.isPresent()) {
            UserTVSeries userTVSeries = userTVSeriesOptional.get();
            model.addAttribute("subscribed", true);

            Integer currentSeasonNum = userTVSeries.getCurrentSeason();
            Integer currentEpisodeNum = userTVSeries.getCurrentEpisode();
            EpisodeWrapper lastEpisodeToAir = tvDetailsWrapper.getLastEpisodeToAir();

            model.addAttribute("currentSeason", currentSeasonNum);
            model.addAttribute("seasonWatched", false);

            if(currentSeasonNum > 0){
                Optional<SeasonWrapper> seasonDetailsOpt = tmdbSeriesService.getSeasonDetails(tmdbId, currentSeasonNum, language);
                if (!seasonDetailsOpt.isPresent())
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND);


                SeasonWrapper seasonWrapper = seasonDetailsOpt.get();

                model.addAttribute("seasonWatched", seasonWrapper.getEpisodes().stream().noneMatch(e -> e.getEpisodeNumber() > currentEpisodeNum));
            }

            model.addAttribute("lastEpisodeWatched", false);

            if(lastEpisodeToAir != null) {
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
    public String getSeason(@PathVariable("tmdbId") Integer tmdbId,
                            @PathVariable("seasonNumber") Integer seasonNumber,
                            @RequestParam(value = "episodeToOpen", required = false) Integer episodeToOpen,
                            @CookieValue(value = "language", defaultValue = "en") Language language,
                            @CookieValue(value = "mode", defaultValue = "movie") Mode mode,
                            Principal principal,
                            Model model) {

        User user = userService.findByLogin(principal.getName());
        model.addAttribute("language", language);
        model.addAttribute("mode", mode);

//        default values
        model.addAttribute("subscribed", false);
        model.addAttribute("episodeToOpen", episodeToOpen != null ? episodeToOpen : 0);


        Optional<TVDetailsWrapper> series = tmdbSeriesService.getTVDetails(tmdbId, language);
        if (!series.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        Optional<SeasonWrapper> season = tmdbSeriesService.getSeasonDetails(tmdbId, seasonNumber, language);
        if (!season.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        TVDetailsWrapper seriesWrapper = series.get();
        SeasonWrapper seasonWrapper = season.get();

        model.addAttribute("series", seriesWrapper);
        model.addAttribute("season", seasonWrapper);

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
    public ResponseEntity subscribe(@RequestParam(value = "tmdbId") Integer tmdbId,
                                    Principal principal) {

        String login = principal.getName();
        User user = userService.findByLogin(login);

        dbSeriesService.subscribeUser(tmdbId, user);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/unSubscribe")
    public ResponseEntity unSubscribe(@RequestParam(value = "tmdbId") Integer tmdbId,
                                      Principal principal) {

        String login = principal.getName();
        User user = userService.findByLogin(login);

        dbSeriesService.unSubscribeUser(tmdbId, user);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public String search(@RequestParam("query") String query,
                         @RequestParam(required = false, name = "year") Integer year,
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

        Optional<TVSearchWrapper> optionalSearchResult = tmdbSeriesService.search(query, page, year, language);

        if (!optionalSearchResult.isPresent()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        TVSearchWrapper searchWrapper = optionalSearchResult.get();

        Map<TVWrapper, Boolean> filmsWithSubFlags = searchWrapper.getResults().stream()
                .collect(toMap(
                        f -> f,
                        f -> dbSeriesService.isExistsByTmdbIdAndUserId(f.getId(), user.getId()),
                        (f1, f2) -> f1,
                        LinkedHashMap::new));

        model.addAttribute("series", filmsWithSubFlags);

        model.addAttribute("pageCount", searchWrapper.getTotalPages());
        model.addAttribute("page", page);

        return "seriesSearchResult";
    }

    @GetMapping("/api/search")
    @ResponseBody
    public ResponseEntity<TVSearchWrapper> searchApi(@RequestParam("query") String query,
                         @RequestParam(required = false, name = "year") Integer year,
                         @RequestParam(required = false, name = "page") Integer page,
                         @CookieValue(value = "language", defaultValue = "en") Language language){

        Optional<TVSearchWrapper> optionalSearchResult = tmdbSeriesService.search(query, page, year, language);
        if (!optionalSearchResult.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }

        return ResponseEntity.ok(optionalSearchResult.get());
    }

    @GetMapping("/subscriptions")
    public String getSubs(@RequestParam(value = "page", required = false) Integer page,
                          @RequestParam(value = "statuses", required = false) List<Status> tvStatuses,
                          @RequestParam(value = "sortBy", required = false) TVSeriesSortBy sortBy,
                          @RequestParam(value = "watch_status", required = false) List<WatchStatus> watchStatuses,
                          @CookieValue(value = "subsMode", defaultValue = "false") String subsMode, 
                          @CookieValue(value = "language", defaultValue = "en") Language language,
                          @CookieValue(value = "mode", defaultValue = "movie") Mode mode,
                          HttpServletResponse response,
                          Principal principal,
                          Model model) {

        if (page == null)
            page = 0;

        if (tvStatuses == null) {
            tvStatuses = emptyList();
        }

        if (watchStatuses == null) {
            watchStatuses = emptyList();
        }

        Boolean viewMode = new Boolean(subsMode);

        User user = userService.findByLogin(principal.getName());

        if(sortBy == null){
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

        Page<UserTVSeries> userTVSeries = dbSeriesService.getByUserAndTVStatusesAndWatchStatusesWithOrderAndPages(tvStatuses, watchStatuses, sortBy, user, page, size);
        List<UserTVSeries> series = userTVSeries.getContent();

        List<TVDetailsWrapper> subscriptions = null;

        if (viewMode){
            subscriptions = series.stream()
                    .map(UserTVSeries::getTvSeries)
                    .map(s -> TVDetailsWrapper.of(s, language)).collect(toList());
        } else {
            subscriptions = series.stream()
                    .map(f -> tmdbSeriesService.getTVDetails(f.getId().getTvSeriesId().intValue(), language))
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
    public String discover(@ModelAttribute("criteria") DiscoverSearchCriteria criteria,
                           @RequestParam(value = "page", required = false) Integer page,
                           @CookieValue(value = "language", defaultValue = "en") Language language,
                           @CookieValue(value = "mode", defaultValue = "movie") Mode mode,
                           Principal principal,
                           Model model) {

        User user = userService.findByLogin(principal.getName());

        if (page == null)
            page = 1;

        // assigning values for service
        criteria.setPage(page);
        criteria.setLanguage(language);

        Optional<TVSearchWrapper> optionalMovieSearch = discoverService.discoverSeries(criteria);

        if (optionalMovieSearch.isPresent()) {

            TVSearchWrapper movieSearchWrapper = optionalMovieSearch.get();

            Map<TVWrapper, Boolean> filmsWithSubFlags = movieSearchWrapper.getResults().stream()
                    .collect(toMap(f -> f,
                            f -> dbSeriesService.isExistsByTmdbIdAndUserId(f.getId(), user.getId()),
                            (f1, f2) -> f1,
                            LinkedHashMap::new));
            model.addAttribute("series", filmsWithSubFlags);

            model.addAttribute("pageCount", movieSearchWrapper.getTotalPages() > 1000 ? 1000 : movieSearchWrapper.getTotalPages());
            model.addAttribute("page", page);
        }

        model.addAttribute("language", language);
        model.addAttribute("mode", mode);

        // adding all attributes to form
        model.addAttribute("companies", companyService.getCompanies(criteria.getCompanies(), language));
        model.addAttribute("networks", networkService.getNetworks(criteria.getNetworks()));
        model.addAttribute("criteria", criteria);

        // adding genreWrappers to form
        model.addAttribute("genres", genreService.getTVGenres(language));

        return "discoverSeries";
    }

    @PostMapping("/setSeasonAndEpisode")
    public ResponseEntity setCurrentSeasonAndEpisode(@RequestParam(value = "tmdbId") Integer tmdbId,
                                                     @RequestParam(value = "season") Integer season,
                                                     @RequestParam(value = "episode") Integer episode,
                                                     Principal principal) {

        User user = userService.findByLogin(principal.getName());

        dbSeriesService.setSeasonAndEpisode(tmdbId, user, season, episode);

        return ResponseEntity.ok().build();
    }
}
