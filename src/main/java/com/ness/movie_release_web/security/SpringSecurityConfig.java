package com.ness.movie_release_web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    @Autowired
    @Qualifier("myUserDetailService")
    private UserDetailsService userDetailsService;

    @Autowired
    private TokenAuthFilter tokenAuthFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterAfter(tokenAuthFilter, BasicAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/", "/home", "/register", "/img/**", "/css/**", "/js/**", "/**/favicon.ico", "/login", "/register").permitAll()
                .antMatchers("/user/**", "/userInfo").hasAnyRole("USER")
                .anyRequest().anonymous()
                .and()
                .logout().logoutUrl("/logout").logoutSuccessUrl("/home").deleteCookies("authorization").permitAll()
                .and()
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }
}
