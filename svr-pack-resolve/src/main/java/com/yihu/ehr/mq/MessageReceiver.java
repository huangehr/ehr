package com.yihu.ehr.mq;

import com.yihu.ehr.task.PackageResolveJob;
import com.yihu.ehr.util.log.LogService;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.31 17:03
 */
@Component
public class MessageReceiver implements XReceiver {
    @Autowired
    PackageResolveJob resolveJob;

    @Override
    public void receive(Object message) {
        try {
            String id = (String)message;

            resolveJob.execute(id);
        } catch (Exception e) {
            LogService.getLogger().error(e.getMessage());
        }
    }
}
