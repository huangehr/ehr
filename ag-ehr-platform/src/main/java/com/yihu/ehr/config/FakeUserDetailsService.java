package com.yihu.ehr.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.02 17:11
 */
@Service
public class FakeUserDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!username.equals("user")) {
            throw new UsernameNotFoundException("User name " + username + " not found.");
        }

        return new User(username, "gateway", getGrantedAuthorities(username));
    }

    private Collection<? extends GrantedAuthority> getGrantedAuthorities(String username) {
        Collection<? extends GrantedAuthority> authorities;
        if (username.equals("user")) {
            authorities = Arrays.asList(() -> "ROLE_ADMIN", () -> "ROLE_USER");
        } else {
            authorities = Arrays.asList(() -> "ROLE_USER");
        }

        return authorities;
    }
}
