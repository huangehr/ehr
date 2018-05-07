package com.yihu.ehr.basic.apps.service;

import com.yihu.ehr.basic.apps.dao.*;
import com.yihu.ehr.basic.apps.model.App;
import com.yihu.ehr.basic.apps.model.AppApi;
import com.yihu.ehr.basic.apps.model.UserApp;
import com.yihu.ehr.basic.user.dao.RoleApiRelationDao;
import com.yihu.ehr.basic.user.dao.RoleAppRelationDao;
import com.yihu.ehr.basic.user.dao.RoleReportRelationDao;
import com.yihu.ehr.basic.user.dao.RolesDao;
import com.yihu.ehr.basic.user.entity.RoleApiRelation;
import com.yihu.ehr.basic.user.entity.RoleAppRelation;
import com.yihu.ehr.basic.user.entity.RoleReportRelation;
import com.yihu.ehr.basic.user.entity.Roles;
import com.yihu.ehr.entity.oauth2.OauthClientDetails;
import com.yihu.ehr.query.BaseJpaService;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
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
public class AppService extends BaseJpaService<App, AppDao> {

    private static final int AppIdLength = 10;
    private static final int AppSecretLength = 16;

    @Autowired
    private UserAppDao userAppDao;
    @Autowired
    private AppDao appDao;
    @Autowired
    private OauthClientDetailsDao oauthClientDetailsDao;
    @Autowired
    private RoleAppRelationDao roleAppRelationDao;
    @Autowired
    private RoleApiRelationDao roleApiRelationDao;
    @Autowired
    private RolesDao rolesDao;
    @Autowired
    private AppApiDao appApiDao;
    @Autowired
    private AppApiErrorCodeDao appApiErrorCodeDao;
    @Autowired
    private AppApiParameterDao appApiParameterDao;
    @Autowired
    private AppApiResponseDao appApiResponseDao;
    @Autowired
    private AppFeatureDao appFeatureDao;
    @Autowired
    private ReportCategoryAppRelationDao reportCategoryAppRelationDao;
    @Autowired
    private RsAppResourceDao rsAppResourceDao;
    @Autowired
    private RsAppResourceMetadataDao rsAppResourceMetadataDao;
    @Autowired
    private RoleReportRelationDao roleReportRelationDao;

    @Value("${fast-dfs.public-server}")
    private String fastDfsPublicServers;

    public AppService() {
    }

    public Page<App> getAppList(String sorts, int page, int size){
        AppDao repo = (AppDao)getJpaRepository();
        Pageable pageable = new PageRequest(page, size, parseSorts(sorts));

        return repo.findAll(pageable);
    }

    public App createApp(App app) {
        app.setId(getRandomString(AppIdLength));
        app.setSecret(getRandomString(AppSecretLength));
        app.setCreateTime(new Date());
        app.setStatus("WaitingForApprove");
        appDao.save(app);
        return app;
    }

    /**
     * 检验App与密码是否正确。
     *
     * @param id
     * @param secret
     * @return
     */
    public boolean verifyApp(String id, String secret) {
        App app = appDao.findOne(id);
        return app != null && app.getSecret().equals(secret);
    }

    public boolean isAppNameExists(String name){
        App app = appDao.findByName(name);
        return app != null;
    }

    private static String getRandomString(int length) {
        String str = "abcdefghigklmnopkrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ0123456789";
        StringBuffer buffer = new StringBuffer();

        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(str.length() - 1);//0~61
            buffer.append(str.charAt(number));
        }

        return buffer.toString();
    }

    public void checkStatus(String appId, String status) {
        App app = appDao.getOne(appId);
        //开启：Approved；禁用：Forbidden;
        app.setStatus(status);
        appDao.save(app);
        //是否显示showFlag
        String showFlag="1";
        if(status.equals("Forbidden")){
            showFlag="0";
        }

        List<UserApp> userAppList = findByAppId(appId);
        if(userAppList != null) {
            for (UserApp userApp : userAppList) {
                userApp.setShowFlag(Integer.parseInt(showFlag));
                userAppDao.save(userApp);
            }
        }
    }

    public List<UserApp> findByAppId(String appId){
        return  userAppDao.findByAppId(appId);
    }

    public boolean findByIdAndSecret(String appId, String secret) {
        return appDao.findByIdAndSecret(appId, secret).size()>0;
    }

    /**
     * 机构资源授权获取
     */
    public List<App> getApps(String userId , String catalog, String manageType) {
        String sql =
                "SELECT * FROM (" +
                        "SELECT b.id, b.name as name, b.secret as secret, b.url as url, b.out_url as outUrl, b.creator as creator," +
                        "   b.auditor as auditor, b.create_time as createTime, b.audit_time as auditTime , b.catalog as catalog, b.status as status, " +
                        "   b.description as description, b.org as org, b.code as code," +
                        "   IF(b.icon IS NULL OR b.icon = '','',CONCAT('" + fastDfsPublicServers + "','/',REPLACE(b.icon,':','/'))) AS icon," +
                        "   b.source_type as sourceType, b.release_flag as releaseFlag, b.manage_type AS manageType" +
                        "   FROM apps b " +
                        "LEFT JOIN user_app m on m.app_id=b.id " +
                        "WHERE b.catalog= :catalog AND m.user_id=:userId AND m.show_flag='1' AND b.status='Approved'";
        if (!StringUtils.isEmpty(manageType)) {
            sql += "AND b.manage_type = :manageType";
        }
        sql += ") p ORDER BY p.id";

        SQLQuery query = currentSession().createSQLQuery(sql);
        query.setParameter("userId", userId);
        query.setParameter("catalog", catalog);
        if (!StringUtils.isEmpty(manageType)) {
            query.setParameter("manageType", manageType);
        }
        query.setResultTransformer(Transformers.aliasToBean(App.class));
        return query.list();
    }

    public App findById(String appId) {
        return appDao.findOne(appId);
    }

    public void delete(String appId) {
        List<Roles> rolesList = rolesDao.findByAppId(appId);
        rolesList.forEach(item -> {
            roleReportRelationDao.deleteByRoleId(item.getId());
        });
        roleAppRelationDao.deleteByAppId(appId);
        rolesDao.deleteByAppId(appId);
        userAppDao.deleteByAppId(appId);
        List<AppApi> appApiList = appApiDao.findByAppId(appId);
        appApiList.forEach(item -> {
            roleApiRelationDao.deleteByApiId(item.getId());
            appApiErrorCodeDao.deleteByAppApiId(item.getId());
            appApiParameterDao.deleteByAppApiId(item.getId());
            appApiResponseDao.deleteByAppApiId(item.getId());
        });
        appApiDao.deleteByAppId(appId);
        appFeatureDao.deleteByAppId(appId);
        reportCategoryAppRelationDao.deleteByAppId(appId);
        rsAppResourceDao.deleteByAppId(appId);
        rsAppResourceMetadataDao.deleteByAppId(appId);
        if (oauthClientDetailsDao.findOne(appId) != null) {
            oauthClientDetailsDao.delete(appId);
        }
        appDao.delete(appId);
    }

    public App authClient(App app, OauthClientDetails oauthClientDetails, Roles basicRole, Roles additionRole) {
        App newApp = appDao.save(app);
        oauthClientDetailsDao.save(oauthClientDetails);
        //扩展角色关联
        if (null == rolesDao.findByCodeAndAppId(additionRole.getCode(), additionRole.getAppId())) {
            additionRole = rolesDao.save(additionRole);
        }
        additionRole = rolesDao.findByCodeAndAppId(additionRole.getCode(), additionRole.getAppId());
        RoleAppRelation additionRoleAppRelation = new RoleAppRelation();
        additionRoleAppRelation.setAppId(app.getId());
        additionRoleAppRelation.setRoleId(additionRole.getId());
        if (null == roleAppRelationDao.findRelation(additionRoleAppRelation.getAppId(), additionRoleAppRelation.getRoleId())) {
            roleAppRelationDao.save(additionRoleAppRelation);
        }
        //通过基础开发者角色扩展api关联
        List<RoleApiRelation> roleApiRelationList = roleApiRelationDao.findByRoleId(basicRole.getId());
        for (RoleApiRelation temp : roleApiRelationList) {
            RoleApiRelation roleApiRelation1 = new RoleApiRelation();
            roleApiRelation1.setRoleId(additionRole.getId());
            roleApiRelation1.setApiId(temp.getApiId());
            roleApiRelationDao.save(roleApiRelation1);
        }
        return newApp;
    }

    public List<Map<String,Object>> getAppByParentIdAndUserId(String userId)throws Exception{
        /*String Sql ="SELECT s.*,r.type " +
                    " FROM user_app a " +
                    " LEFT JOIN apps_relation r ON a.app_id = r.app_id " +
                    " LEFT JOIN apps s ON a.app_id = s.id"+
                    " WHERE a.user_id='"+userId+"' AND r.parent_app_id='"+parentAppId+
                    "' AND a.status=0 AND a.show_flag=1 ";*/

        String sql ="SELECT a.* FROM role_user ru LEFT JOIN roles r ON ru.role_id = r.id LEFT JOIN apps a ON r.app_id = a.id WHERE ru.user_id='"+userId+"' and r.name='医生' and a.release_flag=1";


        List<Map<String,Object>> resultList =  jdbcTemplate.queryForList(sql);
        return resultList;
    }

    /**
     * 医生工作站获取 医生授权的应用
     */
    public List<App> getDoctorAppsByType(String userId , String doctorManageType) {
        String sql =
                "SELECT * FROM (" +
                        "SELECT b.id, b.name as name, b.secret as secret, b.url as url, b.out_url as outUrl, b.creator as creator," +
                        "   b.auditor as auditor, b.create_time as createTime, b.audit_time as auditTime , b.catalog as catalog, b.status as status, " +
                        "   b.description as description, b.org as org, b.code as code," +
                        "   IF(b.icon IS NULL OR b.icon = '','',CONCAT('" + fastDfsPublicServers + "','/',REPLACE(b.icon,':','/'))) AS icon," +
                        "   b.source_type as sourceType, b.release_flag as releaseFlag, b.manage_type AS manageType" +
                        "   FROM apps b " +
                        "LEFT JOIN user_app m on m.app_id=b.id " +
                        "WHERE  m.user_id=:userId AND m.show_flag='1' AND b.status='Approved'";
        if (!StringUtils.isEmpty(doctorManageType)) {
            sql += "AND b.doctor_manage_type = :doctorManageType";
        }
        sql += ") p ORDER BY p.id";

        SQLQuery query = currentSession().createSQLQuery(sql);
        query.setParameter("userId", userId);
        if (!StringUtils.isEmpty(doctorManageType)) {
            query.setParameter("doctorManageType", doctorManageType);
        }
        query.setResultTransformer(Transformers.aliasToBean(App.class));
        return query.list();
    }
}