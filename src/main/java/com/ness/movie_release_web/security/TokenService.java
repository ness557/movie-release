package com.ness.movie_release_web.security;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface TokenService {

    String getToken(UserDetails user);
    Optional<UserDetails> getUser(String token);
}
