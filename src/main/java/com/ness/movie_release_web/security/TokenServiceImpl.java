package com.ness.movie_release_web.security;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    @Value("${my_jwt.seed}")
    private String secret;

    private final UserDetailsService myUserDetailService;

    @Override
    public String getToken(UserDetails user) {

        Map<String, Object> tokenData = new HashMap<>();

        tokenData.put("clientType", "user");
        tokenData.put("username", user.getUsername());
        tokenData.put("password", user.getPassword());
        tokenData.put("token_create_time", new Date().getTime());

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 5);
        Date expDate = calendar.getTime();

        tokenData.put("token_expiration_time", calendar.getTime());
        JwtBuilder jwtBuilder = Jwts.builder();

        jwtBuilder.setExpiration(expDate);
        jwtBuilder.setClaims(tokenData);

        return jwtBuilder.signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    @Override
    public Optional<UserDetails> getUser(String token) {

        Map<String, Object> tokenData = (Map<String, Object>) Jwts.parser().setSigningKey(secret).parse(token).getBody();

        if (tokenData == null) {
            return Optional.empty();
        }

        UserDetails user = myUserDetailService.loadUserByUsername(((String) tokenData.get("username")));

        if(!user.getPassword().equals(tokenData.get("password"))){
            return Optional.empty();
        }

        return Optional.of(user);
    }
}
