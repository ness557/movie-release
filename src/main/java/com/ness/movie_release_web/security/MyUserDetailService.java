package com.ness.movie_release_web.security;

import com.ness.movie_release_web.repository.UserRepository;
import com.ness.movie_release_web.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MyUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        if (!userRepository.existsByLogin(username)) {
            throw new UsernameNotFoundException("No such user: " + username);
        }

        com.ness.movie_release_web.model.User user = userRepository.findByLogin(username);

        Set<GrantedAuthority> authoritySet = new HashSet<>();
        Arrays.stream(user.getRole().split(";")).forEach(e -> authoritySet.add(new SimpleGrantedAuthority(e)));

        return new User(user.getLogin(), user.getEncPassword(), authoritySet);
    }
}
