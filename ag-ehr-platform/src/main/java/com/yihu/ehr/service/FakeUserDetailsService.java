package com.yihu.ehr.service;

import com.yihu.ehr.feign.UserClient;
import com.yihu.ehr.model.user.MUser;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    UserClient userClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MUser user = userClient.getUserByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户" + username + "不存在");
        }

        return new User(username, user.getPassword(), getGrantedAuthorities(username));
    }

    private Collection<? extends GrantedAuthority> getGrantedAuthorities(String username) {
        Collection<? extends GrantedAuthority> authorities;
        authorities = Arrays.asList(() -> "ROLE_USER");

        return authorities;
    }
}
