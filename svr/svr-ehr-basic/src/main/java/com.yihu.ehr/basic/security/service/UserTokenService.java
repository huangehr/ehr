package com.yihu.ehr.basic.security.service;

import com.yihu.ehr.basic.security.dao.UserTokenRepository;
import com.yihu.ehr.entity.security.UserToken;
import com.yihu.ehr.util.oauth2.TokenUtil;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Token管理。
 *
 * @author wq
 * @version 1.0
 * @created 02-6月-2015 17:38:05
 */
@Transactional
@Service
public class UserTokenService {

    @PersistenceContext
    protected EntityManager entityManager;
    @Autowired
    private UserTokenRepository tokenRepository;

    public UserToken createUserToken(String userId, String appId) throws Exception {
        String accessToken = TokenUtil.genToken(16);
        String refreshToken = TokenUtil.genToken(16);
        Integer expiresIn = 3600;

        UserToken token = new UserToken();
        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);
        token.setExpiresIn(expiresIn);
        token.setUserId(userId);
        token.setAppId(appId);
        token.setCreateDate(new Date());
        token.setUpdateDate(new Date());

        tokenRepository.save(token);
        return token;
    }

    public UserToken getToken(String tokenId) {
        UserToken token = tokenRepository.findOne(tokenId);

        return token;
    }

    public UserToken getTokenByUserId(String userId, String appId) {
        Session session = entityManager.unwrap(Session.class);
        HashMap resultMap = new HashMap();
        StringBuilder sb = new StringBuilder();

        sb.append(" select token_id	                ");
        sb.append("       ,access_token	            ");
        sb.append("       ,refresh_token	        ");
        sb.append("       ,expires_in	            ");
        sb.append("       ,create_date	            ");
        sb.append("       ,update_date	            ");
        sb.append("   from user_token 	            ");
        sb.append("  where user_id = '" + userId + "' ");
        sb.append("    and app_id  = '" + appId + "'  ");

        String sql = sb.toString();
        SQLQuery sqlQuery = session.createSQLQuery(sql);

        if (sqlQuery.list().size() == 0) {
            return null;

        } else {
            Object[] userTokenInfo = (Object[]) sqlQuery.list().get(0);

            UserToken token = new UserToken();
            token.setTokenId(userTokenInfo[0].toString());
            token.setAppId(appId);
            token.setUserId(userId);
            token.setAccessToken(userTokenInfo[1].toString());
            token.setRefreshToken(userTokenInfo[2].toString());
            token.setExpiresIn((Integer) userTokenInfo[3]);
            token.setCreateDate((Date) userTokenInfo[4]);
            token.setUpdateDate((Date) userTokenInfo[5]);

            return token;
        }
    }

    public List<UserToken> getTokenList(int from, int count) {
        Session session = entityManager.unwrap(Session.class);
        Criteria criteria = session.createCriteria(UserToken.class);
        if (from >= 0 && count > 0) {
            criteria.setFirstResult(from);
            criteria.setMaxResults(count);
        }
        List<UserToken> list = criteria.list();

        return list;
    }

    public boolean revokeToken(String accessToken) {
        Session session = entityManager.unwrap(Session.class);
        Query query = session.createQuery("from UserToken where accessToken= :accessToken");
        List<UserToken> tokens = query.setString("accessToken", accessToken).list();
        if (tokens.size() == 0) {
            return false;
        } else {
            tokenRepository.delete(tokens.get(0).getTokenId());
            return true;
        }
    }

    public void updateToken(UserToken token) {
        tokenRepository.save(token);
    }

    public UserToken refreshAccessToken(String userId, String refreshToken, String appId) throws Exception {
        UserToken token = getTokenByUserId(userId, appId);

        //如果用户的更新授权正确，则重新生成访问授权，并返回
        if (token.getRefreshToken().equals(refreshToken)) {
            String accessToken = TokenUtil.genToken(16);
            String newRefreshToken = TokenUtil.genToken(16);

            token.setAccessToken(accessToken);
            token.setRefreshToken(newRefreshToken);
            token.setUpdateDate(new Date());
            updateToken(token);
            return token;

        } else {
            return null;
        }
    }
}
