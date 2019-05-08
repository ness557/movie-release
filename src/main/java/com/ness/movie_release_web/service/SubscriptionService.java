package com.ness.movie_release_web.service;

public interface SubscriptionService {
    void subscribeToMovie(Long tmdbId, String login);
    void unsubscribeFromMovie(Long tmdbId, String login);
    void subscribeToSeries(Long tmdbId, String login);
    void unsubscribeFromSeries(Long tmdbId, String login);
}
