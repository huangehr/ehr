package com.yihu.ehr.apps.service;

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

    public AppJpaService() {
    }

    public App createApp(App app) {
        app.setId(getRandomString(AppIdLength));
        app.setSecret(getRandomString(AppSecretLength));
        app.setCreateTime(new Date());
        app.setStatus("WaitingForApprove");
        appRepo.save(app);
        return app;
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