package com.yihu.ehr.security.model;
import com.yihu.ehr.security.data.SQLGeneralDAO;
import com.yihu.ehr.util.token.TokenUtil;


import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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

    @Autowired
    private XUserTokenRepository userTokenRepository;

    @Transactional(Transactional.TxType.SUPPORTS)
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
        //saveEntity(userToken);

        return userToken;
    }

    /**
     * 根据授权ID查询用户授权信息
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public UserToken getUserToken(Integer tokenId) {
//        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
//        UserToken token = (XUserToken) session.get(UserToken.class, tokenId);
        UserToken token = userTokenRepository.findOne(tokenId);

        return token;
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public UserToken getUserTokenByUserId(String userId,String appId) {

        //这里获取User服务和App服务


        //1-1根据用户ID获取用户信息。
        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
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

            User user = userManager.getUser(userId);
            App app = appManager.getApp(appId);

            UserToken userToken = new UserToken();
            userToken.setTokenId(userTokenInfo[0].toString());
            userToken.setApp(app);
            userToken.setUser(user);
            userToken.setAccessToken(userTokenInfo[1].toString());
            userToken.setRefreshToken(userTokenInfo[2].toString());
            userToken.setExpiresIn((Integer) userTokenInfo[3]);
            userToken.setCreateDate((Date) userTokenInfo[4]);
            userToken.setUpdateDate((Date) userTokenInfo[5]);

            return userToken;
        }
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<UserToken> getUserTokenList(int from, int count) {
        Session session = currentSession();
        Criteria criteria = session.createCriteria(UserToken.class);
        if (from >= 0 && count > 0){
            criteria.setFirstResult(from);
            criteria.setMaxResults(count);
        }

        List<XUserToken> list = criteria.list();

        return list;
    }

    /**
     * 删除授权信息
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public void deleteUserToken(String tokenId) {

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        XUserToken token = (XUserToken) session.get(UserToken.class, tokenId);

        deleteEntity(token);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public boolean revokeToken(String accessToken) {

        Map map = new HashMap<>();
        Session session = currentSession();
        Query query = session.createQuery("from UserToken where accessToken= :accessToken");

        List<UserToken> userTokens = query.setString("accessToken", accessToken).list();

        if (userTokens.size() == 0) {
            return false;

        } else {
            deleteEntity(userTokens.get(0));
            return true;
        }
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public void updateUserToken(XUserToken userToken) {
        updateEntity(userToken);
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public XUserToken refreshAccessToken(String userId, String refreshToken,String appId) throws Exception {

        XUserToken userToken = getUserTokenByUserId(userId,appId);

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
