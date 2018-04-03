package com.yihu.ehr.basic.apps.service;

import com.yihu.ehr.basic.apps.dao.AppRepository;
import com.yihu.ehr.basic.apps.dao.OauthClientDetailsDao;
import com.yihu.ehr.basic.apps.dao.UserAppRepository;
import com.yihu.ehr.basic.apps.model.App;
import com.yihu.ehr.basic.apps.model.UserApp;
import com.yihu.ehr.basic.user.dao.XRoleApiRelationRepository;
import com.yihu.ehr.basic.user.dao.XRoleAppRelationRepository;
import com.yihu.ehr.basic.user.dao.XRolesRepository;
import com.yihu.ehr.basic.user.entity.RoleApiRelation;
import com.yihu.ehr.basic.user.entity.RoleAppRelation;
import com.yihu.ehr.basic.user.entity.Roles;
import com.yihu.ehr.entity.oauth2.OauthClientDetails;
import com.yihu.ehr.query.BaseJpaService;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * @author Sand
 * @version 1.0
 * @created 03-8月-2015 16:53:06
 */
@Service
@Transactional
public class AppService extends BaseJpaService<App, AppRepository> {

    private static final int AppIdLength = 10;
    private static final int AppSecretLength = 16;

    @Autowired
    private UserAppRepository userAppRepository;
    @Autowired
    private AppRepository appRepo;
    @Autowired
    private OauthClientDetailsDao oauthClientDetailsDao;
    @Autowired
    private XRoleAppRelationRepository xRoleAppRelationRepository;
    @Autowired
    private XRoleApiRelationRepository xRoleApiRelationRepository;
    @Autowired
    private XRolesRepository xRolesRepository;

    @Value("${fast-dfs.public-server}")
    private String fastDfsPublicServers;

    public AppService() {
    }

    public Page<App> getAppList(String sorts, int page, int size){
        AppRepository repo = (AppRepository)getJpaRepository();
        Pageable pageable = new PageRequest(page, size, parseSorts(sorts));

        return repo.findAll(pageable);
    }

    public App createApp(App app) {
        app.setId(getRandomString(AppIdLength));
        app.setSecret(getRandomString(AppSecretLength));
        app.setCreateTime(new Date());
        app.setStatus("WaitingForApprove");
        appRepo.save(app);

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
        App app = appRepo.findOne(id);
        return app != null && app.getSecret().equals(secret);
    }

    public boolean isAppNameExists(String name){
        App app = appRepo.findByName(name);
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
        App app = appRepo.getOne(appId);
        //开启：Approved；禁用：Forbidden;
        app.setStatus(status);
        appRepo.save(app);
        //是否显示showFlag
        String showFlag="1";
        if(status.equals("Forbidden")){
            showFlag="0";
        }

        List<UserApp> userAppList = findByAppId(appId);
        if(userAppList != null) {
            for (UserApp userApp : userAppList) {
                userApp.setShowFlag(Integer.parseInt(showFlag));
                userAppRepository.save(userApp);
            }
        }
    }

    public List<UserApp> findByAppId(String appId){
        return  userAppRepository.findByAppId(appId);
    }

    public boolean findByIdAndSecret(String appId, String secret) {
        return appRepo.findByIdAndSecret(appId, secret).size()>0;
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
        return appRepo.findOne(appId);
    }

    public App authClient(App app, OauthClientDetails oauthClientDetails, Roles basicRole, Roles additionRole) {
        App newApp = appRepo.save(app);
        oauthClientDetailsDao.save(oauthClientDetails);
        //扩展角色关联
        if (null == xRolesRepository.findByCodeAndAppId(additionRole.getCode(), additionRole.getAppId())) {
            additionRole = xRolesRepository.save(additionRole);
        }
        additionRole = xRolesRepository.findByCodeAndAppId(additionRole.getCode(), additionRole.getAppId());
        RoleAppRelation additionRoleAppRelation = new RoleAppRelation();
        additionRoleAppRelation.setAppId(app.getId());
        additionRoleAppRelation.setRoleId(additionRole.getId());
        if (null == xRoleAppRelationRepository.findRelation(additionRoleAppRelation.getAppId(), additionRoleAppRelation.getRoleId())) {
            xRoleAppRelationRepository.save(additionRoleAppRelation);
        }
        //通过基础开发者角色扩展api关联
        List<RoleApiRelation> roleApiRelationList = xRoleApiRelationRepository.findByRoleId(basicRole.getId());
        for (RoleApiRelation temp : roleApiRelationList) {
            RoleApiRelation roleApiRelation1 = new RoleApiRelation();
            roleApiRelation1.setRoleId(additionRole.getId());
            roleApiRelation1.setApiId(temp.getApiId());
            xRoleApiRelationRepository.save(roleApiRelation1);
        }
        return newApp;
    }

}