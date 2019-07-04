package com.ness.movie_release_web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    @Qualifier("myUserDetailService")
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //@formatter:off
        http.csrf()
                .disable()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
            .authorizeRequests()
                .antMatchers("/", "/home", "/register", "/login", "/register")
                .permitAll()
                .antMatchers("/movie/**", "/company/**",
                        "/network/**", "/series/**", "/people/**",
                        "/userInfo", "/changePassword", "/setLanguage", "/setMode")
                .hasAnyRole("USER")
                .antMatchers("/admin/update_db")
                .hasAnyRole("ADMIN")
                .anyRequest().anonymous()
                .and()
            .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/home")
                .and()
            .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/home")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
                .and()
            .rememberMe()
                .key("rememberKey")
                .alwaysRemember(true);
        //@formatter:on
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/img/**", "/css/**", "/js/**", "/favicon.ico", "/webjars/**");
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
