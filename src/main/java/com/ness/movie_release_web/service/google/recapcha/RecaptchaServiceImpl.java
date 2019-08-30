package com.ness.movie_release_web.service.google.recapcha;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecaptchaServiceImpl implements  RecaptchaService{

    private static final String GOOGLE_RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    @Value("${google.recaptcha.secret}")
    private String recaptchaSecret;

    private final RestTemplateBuilder restTemplateBuilder;

    public Boolean verifyRecaptcha(String ip, String recaptchaResponse) {

        Map<String, String> body = new HashMap<>();
        body.put("secret", recaptchaSecret);
        body.put("response", recaptchaResponse);
        body.put("remoteip", ip);

        log.debug("Request body for recaptcha: {}", body);

        ResponseEntity<Map> recaptchaResponseEntity =
                restTemplateBuilder
                        .build()
                        .postForEntity(GOOGLE_RECAPTCHA_VERIFY_URL +
                                        "?secret={secret}&response={response}&remoteip={remoteip}",
                                        body,
                                        Map.class,
                                        body);

        log.debug("Response from recaptcha: {}", recaptchaResponseEntity);

        Map<String, Object> responseBody = recaptchaResponseEntity.getBody();

        boolean recaptchaSucess = (Boolean) responseBody.get("success");
        if (!recaptchaSucess) {

            log.info("recaptcha error: {}", responseBody.get("error-codes"));

            return Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }
    }
}
