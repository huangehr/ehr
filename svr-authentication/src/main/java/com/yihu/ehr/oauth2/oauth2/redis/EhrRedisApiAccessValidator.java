package com.yihu.ehr.oauth2.oauth2.redis;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 * Created by progr1mmer on 2018/1/10.
 */
@Component
public class EhrRedisApiAccessValidator {

    private static final String DEFAULT_USER_ID_BY_USER_NAME_STATEMENT = "SELECT id FROM users WHERE login_code = ?";
    private static final String DEFAULT_APP_API_STATEMENT = "SELECT CONCAT(aa.micro_service_url, aa.ms_method_name) FROM apps_api aa \n" +
            "\tLEFT JOIN role_api_relation rar ON rar.app_api_id = aa.id \n" +
            "\tWHERE (rar.role_id IN (SELECT role_id FROM role_user WHERE user_id = ?)) \n" +
            "\tAND aa.app_id = ? \n" +
            "\tAND LENGTH(aa.micro_service_url) > 0 AND LENGTH(aa.ms_method_name);";

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void putVerificationApi (String clientId, String userName) {
        List<String> userId = jdbcTemplate.queryForList(DEFAULT_USER_ID_BY_USER_NAME_STATEMENT, new String []{userName}, String.class);
        if(userId.size() <= 0) {
            throw new InsufficientAuthenticationException("Illegal authorized user.");
        }
        List<String> appApiList = jdbcTemplate.queryForList(DEFAULT_APP_API_STATEMENT, new String[]{userId.get(0), clientId}, String.class);
        if(appApiList.size() > 0) {
            Date today = new Date();
            Date tomorrow = DateUtils.addDays(today, 1);
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                tomorrow = dateFormat.parse(dateFormat.format(tomorrow));
            } catch (ParseException e) {
            }
            long expire = tomorrow.getTime() - today.getTime();
            for (String api : appApiList) {
                String key = clientId + "$" + userName + "$" + api;
                if(null == redisTemplate.opsForValue().get(key)) {
                    redisTemplate.opsForValue().set(key, 200);
                    redisTemplate.expire(key, expire, TimeUnit.MILLISECONDS);
                }
            }
        }
    }

    public boolean verificationApi(String clientId, String userName, String api) {
        String key = clientId + "$" + userName + "$" + api;
        Serializable serializable = redisTemplate.opsForValue().get(key);
        if(serializable != null) {
            int count = new Integer(serializable.toString());
            if(count > 0 ) {
                long expire = redisTemplate.getExpire(key);
                redisTemplate.opsForValue().set(key, --count);
                redisTemplate.expire(key, expire, TimeUnit.SECONDS);
                return true;
            }else {
                return false;
            }
        }
        return false;
    }

}
