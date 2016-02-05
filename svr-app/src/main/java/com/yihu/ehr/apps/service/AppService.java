package com.yihu.ehr.apps.service;

import com.yihu.ehr.apps.feign.ConventionalDictClient;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.util.ObjectId;
import com.yihu.ehr.util.query.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaQuery;
import java.util.*;

/**
 * @author Sand
 * @version 1.0
 * @created 03-8月-2015 16:53:06
 */
@Service
@Transactional
public class AppService extends BaseService<App, XAppRepository> {
    private static final int AppIdLength = 10;
    private static final int AppSecretLength = 16;

    @Autowired
    private XAppRepository appRepo;

    @Autowired
    private ConventionalDictClient conventionalDictClient;

    public AppService() {
    }

    public App createApp(String name, MConventionalDict catalog, String url, String tags, String description, String creator) {
        MConventionalDict status = conventionalDictClient.getAppStatus("WaitingForApprove");

        App app = new App();
        app.setId(getRandomString(AppIdLength));
        app.setName(name);
        app.setCatalog(catalog.getCode());
        app.setCreator(creator);
        app.setSecret(getRandomString(AppSecretLength));
        app.setName(name);
        app.setCatalog(catalog.getCode());
        app.setUrl(url);
        app.setTags(tags);
        app.setDescription(description);
        app.setCreateTime(new Date());
        app.setStatus(status.getCode());
        appRepo.save(app);

        return app;
    }

    public void deleteApp(String id) {
        appRepo.delete(id);
    }

    public App getApp(String id) {
        App app = appRepo.findOne(id);

        return app;
    }

    public void updateApp(App app) {
        appRepo.save(app);
    }

    /**
     * 检验App与密码是否正确。
     *
     * @param id
     * @param secret
     * @return
     */
    public boolean verifyApp(String id, String secret) {
        App app = getApp(id);

        return app != null && getApp(id).getSecret().equals(secret);
    }

    /**
     * 搜索应用列表，并以列表形式返回。
     *
     * @param query
     */
    public List<App> searchApps(CriteriaQuery query, int page, int size) {
        return entityManager
                .createQuery(query)
                .setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultList();
        /*Session session = entityManager.unwrap(org.hibernate.Session.class);
        //参数获取处理
        String appId = (String) args.get("appId");
        String appName = (String) args.get("appName");
        String catalog = (String) args.get("catalog");
        String status = (String) args.get("status");
        Integer page = Integer.parseInt(args.get("page").toString());
        Integer pageSize = Integer.parseInt(args.get("rows").toString());
        //动态SQL文拼接
        StringBuilder sb = new StringBuilder();

        sb.append("   from App 	   ");
        sb.append("  where 1=1     ");

        if (!(args.get("catalog") == null || args.get("catalog").equals("") || args.get("catalog").equals("0"))) {

            sb.append("    and catalog= '" + catalog + "' ");
        }
        if (!(args.get("status") == null || args.get("status").equals("") || args.get("status").equals("0"))) {

            sb.append("    and status= '" + status + "' ");
        }
        if (!(args.get("appId") == null || args.get("appId").equals(""))) {

            sb.append("    and (id like '%" + appId + "%' or name like '%" + appName + "%')   ");
        }
        sb.append("    order by name  ");
        String hql = sb.toString();

        Query query = session.createQuery(hql);

        query.setMaxResults(pageSize);
        query.setFirstResult((page - 1) * pageSize);

        return query.list();*/
    }

    /**
     * 根据条件搜索应用总数量
     *
     * @param query
     */
    public Long getAppCount(CriteriaQuery query) {
        Object value = entityManager.createQuery(query).getSingleResult();
        return (Long)value;
    }

    static String getRandomString(int length) {
        String str = "abcdefghigklmnopkrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ0123456789";
        StringBuffer buffer = new StringBuffer();

        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(str.length() - 1);//0~61
            buffer.append(str.charAt(number));
        }

        return buffer.toString();
    }
}