package com.ness.movie_release_web.service;

public interface SubscriptionService {
    void subscribeToMovie(Integer tmdbId, String login);
    void unsubscribeFromMovie(Integer tmdbId, String login);
    void subscribeToSeries(Integer tmdbId, String login);
    void unsubscribeFromSeries(Integer tmdbId, String login);
}
