package com.yihu.ehr.apps.model;

import com.yihu.ehr.dict.service.AppCatalog;
import com.yihu.ehr.dict.service.AppStatus;
import com.yihu.ehr.dict.service.ConventionalDictEntry;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author Sand
 * @version 1.0
 * @created 03-8月-2015 16:53:06
 */
@Service
@Transactional
public class AppManager  {
    private static final int AppIdLength = 10;
    private static final int AppSecretLength = 16;

    public AppManager() {
    }

    @Autowired
    private ConventionalDictEntry abstractDictEntryManage;

    public App createApp(String name, AppCatalog catalog, String url, String tags, String description, User creator) {

        App app = new App(name, catalog, creator);

        app.setId(new ObjectVersion().toString());
        app.setId(getRandomString(AppIdLength));
        app.setSecret(getRandomString(AppSecretLength));
        app.setName(name);
        app.setCatalog(catalog);
        app.setUrl(url);
        app.setTags(tags);
        app.setDescription(description);

        saveEntity(app);

        return app;
    }

    public void deleteApp(String id) {
        deleteEntity(getEntity(App.class, id));
    }

    public App getApp(String id) {
        return (App) getEntity(App.class, id);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public Object getAppList(int from, int count) {
        Session session = currentSession();
        Criteria criteria = session.createCriteria(App.class);
        if (from >= 0 && count > 0) {
            criteria.setFirstResult(from);
            criteria.setMaxResults(count);
        }

        List<App> list = criteria.list();
        return list;
    }

    public void updateApp(App app) {
        updateEntity(app);
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

    public boolean validationApp(String id, String secret) {

        App app = getApp(id);
        if(app == null){
            return false;
        }else{
            if (getApp(id).getSecret().equals(secret)) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 根据条件搜索应用列表.
     *
     * @param args
     */
    public List<App> searchApps(Map<String, Object> args) {

        //参数获取处理
        String appId = (String) args.get("appId");
        String appName = (String) args.get("appName");
        String catalog = (String) args.get("catalog");
        String status = (String) args.get("status");
        Integer page = (Integer) args.get("page");
        Integer pageSize = (Integer) args.get("rows");

        Session session = currentSession();

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

        return query.list();
    }

    public List<AppDetailModel> searchAppDetailModels(Map<String, Object> args) {

        //参数获取处理
        List<App> appList = searchApps(args);
        List<AppDetailModel> appDetailModels = new ArrayList<>();

        for (App app : appList) {

            AppDetailModel appDetailModel = new AppDetailModel();
            appDetailModel.setId(app.getId());
            appDetailModel.setName(app.getName());
            appDetailModel.setSecret(app.getSecret());
            appDetailModel.setUrl(app.getUrl());
            appDetailModel.setCreator(app.getCreator());
            appDetailModel.setCreate_time(app.getCreateTime());
            appDetailModel.setAudit_time(app.getAuditTime());
            appDetailModel.setCatalog(app.getCatalog());
            AppCatalog appCatalog = abstractDictEntryManage.getAppCatalog(app.getCatalog().getCode());
            //appDetailModel.setCatalogValue(app.getCatalog().getValue());
            appDetailModel.setCatalogValue(appCatalog.getValue());
            appDetailModel.setStatus(app.getStatus());
            //appDetailModel.setStatusValue(app.getStatus().getValue());
            AppStatus appStatus = abstractDictEntryManage.getAppStatus(app.getStatus().getCode());
            appDetailModel.setDescription(app.getDescription());
            appDetailModel.setStrTags(app.getTags());

            appDetailModels.add(appDetailModel);
        }

        return appDetailModels;
    }

    /**
     * 根据条件搜索APP.
     *
     * @param appId
     */
    public AppDetailModel searchAppDetailModel(String appId) {

        Session session = currentSession();

        //动态SQL文拼接
        StringBuilder sb = new StringBuilder();

        sb.append("   from App 	   ");
        sb.append("  where 1=1     ");
        sb.append("    and id= '" + appId + "' ");

        String hql = sb.toString();
        Query query = session.createQuery(hql);

        List<XApp> appList = query.list();
        XApp app = appList.get(0);

        AppDetailModel detailModel = new AppDetailModel();

        detailModel.setId(app.getId());
        detailModel.setSecret(app.getSecret());
        detailModel.setName(app.getName());
        detailModel.setCatalog(app.getCatalog());
        detailModel.setStatus(app.getStatus());
        detailModel.setUrl(app.getUrl());
        detailModel.setDescription(app.getDescription());
        detailModel.setStrTags(app.getTags());

        return detailModel;
    }

    /**
     * 根据条件搜索应用总数量
     *
     * @param args
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public Integer searchAppsInt(Map<String, Object> args) {

        //参数获取处理
        String appId = (String) args.get("appId");
        String appName = (String) args.get("appName");
        String catalog = (String) args.get("catalog");
        String status = (String) args.get("status");
        Integer page = (Integer) args.get("page");
        Integer pageSize = (Integer) args.get("rows");

        Session session = currentSession();

        //动态SQL文拼接
        StringBuilder sb = new StringBuilder();

        sb.append("select 1  from App 	   ");
        sb.append("  where 1=1     ");

        if (!(args.get("catalog") == null || args.get("catalog").equals("") || args.get("catalog").equals("0"))) {

            sb.append("    and catalog= '" + catalog + "' ");
        }
        if (!(args.get("status") == null || args.get("status").equals("") || args.get("status").equals("0"))) {

            sb.append("    and status= '" + status + "' ");
        }
        if (!(args.get("appId") == null || args.get("appId").equals("") || args.get("appId").equals("0"))) {

            sb.append("    and (id like '%" + appId + "%' or name like '%" + appName + "%')   ");
        }

        String hql = sb.toString();

        Query query = session.createQuery(hql);

        return query.list().size();
    }

    /**
     * 审核app状态的方法
     */
    public boolean checkStatus(String appId, AppStatus appStatus) {
        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        App app = (App) session.get(App.class, appId);
        app.setStatus(appStatus);
        saveEntity(app);
        return true;
    }
}