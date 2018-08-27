package com.yihu.ehr.analyze.service.dataQuality;


import com.yihu.ehr.query.BaseJpaService;
import org.apache.commons.collections.map.HashedMap;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 质控管理首页- 基础逻辑类
 *
 * @author HZY
 * @created 2018/8/17 11:24
 */
@Service
public abstract class DataQualityBaseService extends BaseJpaService {

    @Value("${quality.orgCode}")
    private String defaultOrgCode;
    @Value("${quality.cloud}")
    private String cloud;
    @Value("${quality.cloudName}")
    private String cloudName;


    /**
     * 获取市级下的区县质控信息 - 【区域级别】
     *
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
    public abstract List<Map<String, Object>> getAreaDataQuality(Integer dataLevel,String startDate, String endDate) throws Exception;

    /**
     * 获取区县下的机构质控信息 - 【机构级别】
     *
     * @param areaCode  区域编码
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
    public abstract List<Map<String, Object>> getOrgDataQuality(Integer dataLevel,String areaCode, String startDate, String endDate) throws Exception;


    /**
     * 获取医院列表
     *
     * @return
     */
    public Map<String, Object> getOrgMap() {
        Session session = currentSession();
        //获取医院数据
        Query query1 = session.createSQLQuery("SELECT org_code,full_name from organizations where org_type = 'Hospital' ");
        List<Object[]> orgList = query1.list();
        Map<String, Object> orgMap = new HashedMap();
        orgList.forEach(one -> {
            String orgCode = one[0].toString();
            String name = one[1].toString();
            orgMap.put(orgCode, name);
        });
        return orgMap;
    }


    /**
     * 通过map中的rate字段降序排雷
     * @param list
     */
    public void comparator(List<Map<String, Object>> list ){
        Collections.sort(list, new Comparator<Map<String, Object>>() {
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                return getDoubleValue(o2.get("rate")).compareTo(getDoubleValue(o1.get("rate")));
            }
        });
        list.forEach(map->{
            map.put("rate",map.get("rate") + "%");
        });
    }


    /**
     * 百分比计算（不带单位）
     * @param molecular
     * @param denominator
     * @return
     */
    public Double calDoubleRate(double molecular, double denominator) {
        if (molecular == 0) {
            return 0.00;
        } else if (denominator == 0) {
            return 100.00;
        }

        BigDecimal b = new BigDecimal((molecular/denominator) *100) ;
        return b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }



    /**
     * 百分比计算
     *
     * @param molecular   分子
     * @param denominator 分母
     * @return
     */
    public String calRate(Integer molecular, Integer denominator) {
        if (molecular == 0) {
            return "0.00%";
        } else if (denominator == 0) {
            return "100.00%";
        }
        DecimalFormat decimalFormat = new DecimalFormat("0.00%");
        return decimalFormat.format(molecular / denominator);
    }

    /**
     * 获取douuble值
     *
     * @param objValue
     * @return
     */
    public Double getDoubleValue(Object objValue) {
        double value = 0;
        try {
            if (objValue != null)
                value = Double.parseDouble(objValue.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

}
