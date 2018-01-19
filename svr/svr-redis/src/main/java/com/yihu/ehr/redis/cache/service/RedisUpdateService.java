package com.yihu.ehr.redis.cache.service;

import com.yihu.ehr.redis.schema.OrgKeySchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by Sxy on 2017/08/28.
 * Redis更新服务
 */
@Service
public class RedisUpdateService {

    @Autowired
    private OrgKeySchema orgKeySchema;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 更新机构名称缓存
     * @param orgCode
     * @return
     */
    public boolean updateOrgName(String orgCode) {
        String sql = "select full_name from organizations where org_code = '" + orgCode + "'";
        try {
            Map<String, Object> resultMap = jdbcTemplate.queryForMap(sql);
            orgKeySchema.set(orgCode, String.valueOf(resultMap.get("full_name")));
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 更新机构区域缓存
     * @param orgCode
     * @return
     */
    public boolean updateOrgArea(String orgCode) {
        String sql = "select administrative_division from organizations where org_code = '" +  orgCode + "'";
        try {
            Map<String, Object> resultMap = jdbcTemplate.queryForMap(sql);
            orgKeySchema.setOrgArea(orgCode, String.valueOf(resultMap.get("administrative_division")));
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 更新机构Saas区域缓存
     * @param orgCode
     * @return
     */
    public boolean updateOrgSaasArea(String orgCode) {
        String sql = "select saas_code from org_saas where type='1' and org_code = '" + orgCode  + "' order by saas_code";
        try {
            List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql);
            String value = "";
            for(Map<String, Object> tempMap : resultList) {
                String saasCode = String.valueOf(tempMap.get("saas_code"));
                value += saasCode + ",";
            }
            if(value.equals("")) {
                return false;
            }
            orgKeySchema.setOrgSaasArea(orgCode, value.substring(0, value.length() - 1));
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 更新机构Saas机构缓存
     * @param orgCode
     * @return
     */
    public boolean updateOrgSaasOrg(String orgCode) {
        String sql = "select saas_code from org_saas where type='2' and org_code = '" + orgCode  + "' order by saas_code";
        try {
            List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql);
            String value = "";
            for(Map<String, Object> tempMap : resultList) {
                String saasCode = String.valueOf(tempMap.get("saas_code"));
                value += saasCode + ",";
            }
            if(value.equals("")) {
                return false;
            }
            orgKeySchema.setOrgSaasOrg(orgCode, value.substring(0, value.length() - 1));
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
