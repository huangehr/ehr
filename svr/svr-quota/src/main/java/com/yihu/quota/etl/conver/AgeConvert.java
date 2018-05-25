package com.yihu.quota.etl.conver;

import com.yihu.quota.etl.Contant;
import com.yihu.quota.model.jpa.dimension.TjQuotaDimensionSlave;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by janseny on 2018/5/9.
 * 年龄转换
 */
public class AgeConvert implements Convert {

    private static String EHR_003905 = "EHR_003905";  // 门诊年龄
    private static String EHR_005013 = "EHR_005013";  // 住院年龄


    /**
     * @param dataList  数据
     * @return
     */
    public List<Map<String, Object>> convert(JdbcTemplate jdbcTemplate, List<Map<String, Object>> dataList , TjQuotaDimensionSlave slave ) {
        System.out.println("run in age Conver");
        Map<String,Object> tempMap = new HashMap<>();
        List<Map<String, Object>> result = new ArrayList<>();
        for(Map<String, Object> one:dataList) {
            try {
                //TODO 业务逻辑处理
                int age = 0;
                String ageStr = "";
                String oldAgeStr = "";
                tempMap = one;
                if(one.get(EHR_003905) != null ||  one.get(EHR_005013) != null ){
                    String key = "";
                    if(one.get(EHR_003905) != null){
                        ageStr = one.get(EHR_003905).toString();
                        key = EHR_003905;
                    }else  if(one.get(EHR_005013) != null){
                        ageStr = one.get(EHR_005013).toString();
                        key = EHR_005013;
                    }
                    oldAgeStr = ageStr;
                    boolean falg = false;
                    if(ageStr.contains("岁") ){
                        ageStr = ageStr.substring(0,ageStr.indexOf("岁"));
                        falg = true;
                    }else if(ageStr.contains("月")){
                        ageStr = "0";
                        falg = true;
                    }else if(ageStr.contains("天")){
                        ageStr = "0";
                        falg = true;
                    }else{
                        if(ageStr.contains(".")){
                            ageStr = ageStr.substring(0,ageStr.indexOf("."));
                            falg = true;
                        }
                    }
                    System.out.println("run in age Conver ageStr = " + ageStr);
                    if(falg){
                        age = Integer.parseInt(ageStr);
                        String ageLevel = getAgeCode(age);
                        tempMap.put(key,ageLevel);
                        if(one.get("$statisticsKey") != null){
                            String statisticsKey = one.get("$statisticsKey").toString();
                            tempMap.put("$statisticsKey",statisticsKey.replaceAll(oldAgeStr,ageLevel));
                        }
                        result.add(tempMap);
                    }
                }else{
                    result.add(tempMap);
                }
            } catch (Exception e) {
               throw  new NumberFormatException("年龄转换有误！" + e.getMessage());
            }
        };
        return result;
    }

    // TODO 从数据字典中提取 年龄段区间
    public String getAgeCode(Integer age) {
        if (age <= 6) {
            return Contant.convert.level_age_1;
        } else if (age >= 7 && age <= 17) {
            return Contant.convert.level_age_2;
        } else if (age >= 18 && age <= 40) {
            return Contant.convert.level_age_3;
        } else if (age >= 41 && age < 65) {
            return Contant.convert.level_age_4;
        } else {
            return Contant.convert.level_age_5;
        }
    }

    public String converAge(String ageStr) {
            int age = 0;
            if(ageStr.contains("岁") ){
                ageStr = ageStr.substring(0,ageStr.indexOf("岁"));
            }else if(ageStr.contains("月")){
                ageStr = "0";
            }else{
                if(ageStr.contains(".")){
                    ageStr = ageStr.substring(0,ageStr.indexOf("."));
                }
            }
            age = Integer.valueOf(ageStr);
            return getAgeCode(age);
    }


}
