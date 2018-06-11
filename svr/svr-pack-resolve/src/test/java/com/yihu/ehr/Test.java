package com.yihu.ehr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.http.IPInfoUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by progr1mmer on 2018/4/10.
 */
public class Test {

    @org.junit.Test
    public void test() throws Exception {
        String day1 = "2018-01-02 01:02:03";
        String day2 = "2018-01-04 01:02:04";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long time = dateFormat.parse(day2).getTime() - dateFormat.parse(day1).getTime();
        System.out.println(time);
        long day;
        if (time % (1000 * 60 * 60 * 24) > 0) {
            day = time / (1000 * 60 * 60 * 24) + 1;
        } else {
            day = time / (1000 * 60 * 60 * 24);
        }
        System.out.println(day);
    }


}
