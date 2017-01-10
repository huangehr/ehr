package com.yihu.ehr.apps.service;

import com.yihu.ehr.apps.model.App;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
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
        app.setStatus(status);
        appRepo.save(app);
    }

    public boolean findByIdAndSecret(String appId, String secret) {
        return appRepo.findByIdAndSecret(appId,secret).size()>0;
    }

}