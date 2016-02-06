package com.yihu.ehr.apps.service;

import com.yihu.ehr.apps.feign.ConventionalDictClient;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class AppJpaService extends BaseJpaService<App, XAppRepository> {
    private static final int AppIdLength = 10;
    private static final int AppSecretLength = 16;

    @Autowired
    private XAppRepository appRepo;

    @Autowired
    private ConventionalDictClient conventionalDictClient;

    public AppJpaService() {
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
}