package com.yihu.ehr.apps.service;

import com.yihu.ehr.apps.model.App;
import com.yihu.ehr.apps.model.UserApp;
import com.yihu.ehr.model.app.MApp;
import com.yihu.ehr.query.BaseJpaService;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
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
public class AppService extends BaseJpaService<App, XAppRepository> {
    private static final int AppIdLength = 10;
    private static final int AppSecretLength = 16;
    @Autowired
    private XUserAppRepository userAppRepository;
    @Autowired
    private XAppRepository appRepo;
    public AppService() {
    }

    public Page<App> getAppList(String sorts, int page, int size){
        XAppRepository repo = (XAppRepository)getJpaRepository();
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
        return appRepo.findByIdAndSecret(appId,secret).size()>0;
    }

    /**
     * 机构资源授权获取
     */
    public List<App> getAppsByUserIdAndCatalog(String userId,String catalog)
    {
        String sql =
                "SELECT * FROM (" +
                        "SELECT b.id, b.name as name, b.secret as secret, b.url as url, b.creator as creator," +
                        "b.auditor as auditor, b.create_time as createTime, b.audit_time as auditTime , b.catalog as catalog, b.status as status, " +
                        "b.description as description, b.org as org, b.code as code," +
                        " b.source_type as sourceType, b.release_flag as releaseFlag" +
                        " FROM apps b " +
                        "LEFT JOIN user_app m on m.app_id=b.id " +
                        "WHERE b.catalog= :catalog AND m.user_id=:userId AND m.show_flag='1'" +
                        ") p ORDER BY p.id";

        SQLQuery query = currentSession().createSQLQuery(sql);
        query.setParameter("userId", userId);
        query.setParameter("catalog", catalog);
        query.setResultTransformer(Transformers.aliasToBean(App.class));
        return query.list();
    }

}