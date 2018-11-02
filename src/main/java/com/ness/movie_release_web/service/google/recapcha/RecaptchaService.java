package com.ness.movie_release_web.service.google.recapcha;

public interface RecaptchaService {
    Boolean verifyRecaptcha(String ip, String recaptchaResponse);
}
