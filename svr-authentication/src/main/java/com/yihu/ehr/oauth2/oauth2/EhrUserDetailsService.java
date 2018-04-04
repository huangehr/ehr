package com.yihu.ehr.oauth2.oauth2;

import com.yihu.ehr.oauth2.model.EhrUserDetails;
import com.yihu.ehr.oauth2.model.EhrUserSimple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.02 17:11
 */
@Service
public class EhrUserDetailsService implements UserDetailsService {

    private static final String DEFAULT_USER_DETAILS_STATEMENT = "SELECT * FROM users u WHERE u.login_code = ? OR u.telephone = ? OR u.id_card_no = ?";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 用户登录判读接口
     *
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<EhrUserDetails> users = jdbcTemplate.query(DEFAULT_USER_DETAILS_STATEMENT, new BeanPropertyRowMapper(EhrUserDetails.class), username, username, username);
        if (users == null || users.size() == 0) {
            throw new UsernameNotFoundException(username);
        }
        return new User(username, users.get(0).getPassword(), getGrantedAuthorities(username));

        /*if (username.equals("admin")){
            return new User("admin", "e10adc3949ba59abbe56e057f20f883e", getGrantedAuthorities(username));
        }
        return null;*/
    }

    private Collection<? extends GrantedAuthority> getGrantedAuthorities(String username) {
        Collection<GrantedAuthority> authorities = new ArrayList<>(1);
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        return authorities;
    }

    public EhrUserSimple loadUserSimpleByUsername(String username) throws UsernameNotFoundException {
        List<EhrUserSimple> users = jdbcTemplate.query(DEFAULT_USER_DETAILS_STATEMENT, new BeanPropertyRowMapper(EhrUserSimple.class), username, username, username);
        if (users == null || users.size() == 0) {
            throw new UsernameNotFoundException(username);
        }
        return users.get(0);
    }

}
