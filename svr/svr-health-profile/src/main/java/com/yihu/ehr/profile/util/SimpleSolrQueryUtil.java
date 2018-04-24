package com.yihu.ehr.profile.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Created by progr1mmer on 2018/3/26.
 */
public class SimpleSolrQueryUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static String getQuery(String filter, String date, String q) throws Exception {
        Map<String, String> qMap = objectMapper.readValue(q, Map.class);
        String param = qMap.get("q");
        if (!StringUtils.isEmpty(filter)) {
            String [] conditions = filter.split(";");
            for (String condition : conditions) {
                if (condition.split("=").length == 2) {
                    String key = condition.split("=")[0];
                    String value = condition.split("=")[1];
                    param += " AND " + key + ":" + value;
                }
                if (condition.split("\\?").length == 2) {
                    String key = condition.split("\\?")[0];
                    String value = condition.split("\\?")[1];
                    param += " AND " + key + ":*" + value + "*";
                }
                continue;
            }
        }
        if (!StringUtils.isEmpty(date)) {
            Map<String, String> dateMap = objectMapper.readValue(date, Map.class);
            if (!StringUtils.isEmpty(dateMap.get("start"))) {
                param += " AND event_date:[" + dateMap.get("start") + " TO *]";
            }
            if (!StringUtils.isEmpty(dateMap.get("end"))) {
                param += " AND event_date:[* TO " + dateMap.get("end") + "]";
            }
            if (!StringUtils.isEmpty(dateMap.get("month"))) {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
                Date date1 = dateFormat.parse(dateMap.get("month"));
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date1);
                String start = dateMap.get("month") + "-01T00:00:00Z";
                int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                String end = dateMap.get("month") + "-" + maxDay + "T00:00:00Z";
                param += " AND event_date:[" + start + " TO " + end + "]";
            }
        }
        String sort = "{\"event_date\":\"desc\"}";
        qMap.put("sort", sort);
        qMap.put("q", param);
        return objectMapper.writeValueAsString(qMap);
    }
}
