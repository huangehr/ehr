package com.yihu.ehr;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by progr1mmer on 2018/3/26.
 */
public class DateTest {

    public static void main (String [] args) throws Exception {
        String s = "wewe;ewew";
        System.out.println(s.split(";").length);
        String dateStr = "2018-02";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        Date date = dateFormat.parse(dateStr);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        System.out.println(calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
    }

}
