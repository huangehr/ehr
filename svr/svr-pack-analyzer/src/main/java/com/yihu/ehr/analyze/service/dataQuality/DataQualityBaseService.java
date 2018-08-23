package com.yihu.ehr.analyze.service.dataQuality;


import com.yihu.ehr.query.BaseJpaService;
import org.apache.commons.collections.map.HashedMap;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

/**
 *  质控管理首页- 基础逻辑类
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
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
    public abstract List<Map<String,Object>> getAreaDataQuality(String startDate, String endDate) throws Exception;

    /**
     * 获取区县下的机构质控信息 - 【机构级别】
     * @param areaCode 区域编码
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
    public abstract List<Map<String,Object>> getOrgDataQuality(String areaCode ,String startDate, String endDate) throws Exception;


    /**
     * 获取医院列表
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
     * 百分比计算
     *
     * @param molecular   分子
     * @param denominator 分母
     * @return
     */
    public String calRate(double molecular, double denominator) {
        if (molecular == 0) {
            return "0.00%";
        } else if (denominator == 0) {
            return "100.00%";
        }
        DecimalFormat decimalFormat = new DecimalFormat("0.00%");
        return decimalFormat.format(molecular / denominator);
    }

    /**
     *  百分比计算
     * @param molecular    分子
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

}
