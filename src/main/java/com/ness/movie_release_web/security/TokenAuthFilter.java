package com.ness.movie_release_web.security;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Controller
public class TokenAuthFilter extends GenericFilterBean {

    private Logger logger = LoggerFactory.getLogger(getClass().getName());

    @Autowired
    private TokenService authService;

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        final HttpServletRequest httpRequest = (HttpServletRequest) request;

        Cookie[] cookies = httpRequest.getCookies();
        if(cookies == null)
            cookies = new Cookie[0];

        Optional<Cookie> authOpt = Arrays.stream(cookies).filter(e -> "authorization".equals(e.getName())).findFirst();

        if (authOpt.isPresent()) {
            Cookie auth = authOpt.get();
            if (!auth.getValue().startsWith("bearer")) {
                logger.info("wrong auth type: " + auth.getValue());
                ((HttpServletResponse) response).sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "wrong auth type");
                return;
            }

            String[] arr = StringUtils.split(auth.getValue(), ":");
            if (arr.length < 2) {
                logger.info("empty token");
                ((HttpServletResponse) response).sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "empty token");
                return;
            }

            String token = arr[1];

            Optional<UserDetails> userOpt = authService.getUser(token);

            if (!userOpt.isPresent()) {
                logger.info("User from token not found");
                ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED,
                        "User from token not found");
                return;
            }

            UserDetails user = userOpt.get();

            final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    user, user, user.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }
}