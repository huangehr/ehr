package com.yihu.ehr.event.processor;

import com.yihu.ehr.event.model.Event;
import com.yihu.ehr.hbase.HBaseDao;
import com.yihu.ehr.lang.SpringContext;

import java.util.Map;

/**
 * Created by progr1mmer on 2018/7/4.
 */
public class PatientAgeProcessor extends Processor {

    public PatientAgeProcessor(String type) {
        this.type = type;
    }

    @Override
    public void process(Event event) {
        if (active) {
            // deal code
            HBaseDao hBaseDao = SpringContext.getService(HBaseDao.class);
            Map<String, Object> data = hBaseDao.getResultMap("HealthProfile", "49229004X_000001184755_1513577754000");
            System.out.println("PatientAgeProcessor Start : " + data.toString());
        }
    }
}
