package com.yihu.ehr.security.service;


import com.yihu.ehr.util.token.TokenUtil;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wq on 2015/8/3.
 */

@Transactional
@Service
public class TokenManager {

    @PersistenceContext
    protected EntityManager entityManager;

    @Autowired
    private XUserTokenRepository userTokenRepository;

    public UserToken createUserToken(String userId, String appId) throws Exception {

        //生成token信息
        String accessToken = TokenUtil.genToken();
        String refreshToken = TokenUtil.genToken();
        Integer expiresIn = 3600;

        //对生成的token信息进行保存
        UserToken userToken = new UserToken();

        userToken.setAccessToken(accessToken);
        userToken.setRefreshToken(refreshToken);
        userToken.setExpiresIn(expiresIn);
        userToken.setUserId(userId);
        userToken.setAppId(appId);
        userToken.setCreateDate(new Date());
        userToken.setUpdateDate(new Date());

        userTokenRepository.save(userToken);
        return userToken;
    }

    /**
     * 根据授权ID查询用户授权信息
     */
    public UserToken getUserToken(String tokenId) {
        UserToken token = userTokenRepository.findOne(tokenId);
        return token;
    }

    public UserToken getUserTokenByUserId(String userId,String appId) {

        //这里获取User服务和App服务


        //1-1根据用户ID获取用户信息。
        Session session = entityManager.unwrap(org.hibernate.Session.class);
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

//            UserModel user = userClient.getUser(userId);
//            AppModel app = appClient.getApp(appId);

            UserToken userToken = new UserToken();
            userToken.setTokenId(userTokenInfo[0].toString());
            userToken.setAppId(appId);
            userToken.setUserId(userId);
            userToken.setAccessToken(userTokenInfo[1].toString());
            userToken.setRefreshToken(userTokenInfo[2].toString());
            userToken.setExpiresIn((Integer) userTokenInfo[3]);
            userToken.setCreateDate((Date) userTokenInfo[4]);
            userToken.setUpdateDate((Date) userTokenInfo[5]);

            return userToken;
        }
    }

    public List<UserToken> getUserTokenList(int from, int count) {
        Session session = entityManager.unwrap(org.hibernate.Session.class);
        Criteria criteria = session.createCriteria(UserToken.class);
        if (from >= 0 && count > 0){
            criteria.setFirstResult(from);
            criteria.setMaxResults(count);
        }
        List<UserToken> list = criteria.list();

        return list;
    }

    public boolean revokeToken(String accessToken) {
        Session session = entityManager.unwrap(org.hibernate.Session.class);
        Query query = session.createQuery("from UserToken where accessToken= :accessToken");
        List<UserToken> userTokens = query.setString("accessToken", accessToken).list();
        if (userTokens.size() == 0) {
            return false;
        } else {
            userTokenRepository.delete(userTokens.get(0).getTokenId());
            return true;
        }
    }

    public void updateUserToken(UserToken userToken) {
        userTokenRepository.save(userToken);
    }

    public UserToken refreshAccessToken(String userId, String refreshToken,String appId) throws Exception {

        UserToken userToken = getUserTokenByUserId(userId,appId);

        //如果用户的更新授权正确，则重新生成访问授权，并返回
        if (userToken.getRefreshToken().equals(refreshToken)) {
            String accessToken = TokenUtil.genToken();
            String newRefreshToken = TokenUtil.genToken();

            userToken.setAccessToken(accessToken);
            userToken.setRefreshToken(newRefreshToken);
            userToken.setUpdateDate(new Date());
            updateUserToken(userToken);
            return userToken;

        } else {
            return null;
        }
    }
}
