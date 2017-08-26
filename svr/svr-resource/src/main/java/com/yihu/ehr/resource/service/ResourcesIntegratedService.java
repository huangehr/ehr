package com.yihu.ehr.resource.service;


import com.yihu.ehr.entity.health.HealthBusiness;
import com.yihu.ehr.entity.quota.TjQuota;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.intf.ResourcesDao;
import com.yihu.ehr.resource.model.RsMetadata;
import com.yihu.ehr.resource.model.RsResources;
import com.yihu.ehr.resource.service.query.ResourcesQueryService;
import com.yihu.ehr.util.rest.Envelop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.java2d.cmm.kcms.KcmsServiceProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Sxy on 2016/08/01.
 */
@Service
@Transactional
public class ResourcesIntegratedService extends BaseJpaService<RsResources, ResourcesDao> {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ResourcesQueryService resourcesQueryService;

    /**
     * 获取档案数据主体列表
     * @return
     */
    public List<RsResources> findFileMasterList(String filters) {
        String sql = "";
        if (filters != null) {
            sql = "select rr.id, rr.code, rr.name, rr.rs_interface from rs_resources rr where rr.code in (select code from std_data_set_59083976eebd where multi_record = 0) AND rr.name like " + "'%" + filters + "%'";
        } else {
            sql = "select rr.id, rr.code, rr.name, rr.rs_interface from rs_resources rr where rr.code in (select code from std_data_set_59083976eebd where multi_record = 0)";
        }
        RowMapper rowMapper = (RowMapper) BeanPropertyRowMapper.newInstance(RsResources.class);
        return this.jdbcTemplate.query(sql, rowMapper);
    }

    /**
     * 根据档案数据主体获取数据元列表
     * @param rsResources
     * @return
     */
    public List<RsMetadata> findFileMetadataList(RsResources rsResources) {
        String sql = "";
        RowMapper rowMapper = null;
        if(rsResources != null) {
            sql = "select * from rs_metadata rm where rm.id in (select metadata_id from rs_resource_metadata where resources_id = '" + rsResources.getId() + "')";
            rowMapper = (RowMapper) BeanPropertyRowMapper.newInstance(RsMetadata.class);
            return this.jdbcTemplate.query(sql, rowMapper);
        }else {
            return null;
        }
    }

    /**
     * 根据parentId获取指标分类主体列表
     * @return
     */
    public List<HealthBusiness> findHealthBusinessList(int parentId, String filters) {
        String sql = "";
        if(parentId != 0 && filters != null) {
            sql = "select * from health_business where parent_id = " + parentId + " AND name like " + "'%" + filters + "%'";
        }else {
            sql = "select * from health_business where parent_id = " + parentId;
        }
        RowMapper rowMapper = (RowMapper) BeanPropertyRowMapper.newInstance(HealthBusiness.class);
        return this.jdbcTemplate.query(sql, rowMapper);
    }

    /**
     * 根据指标分类获取指标
     * @param healthBusiness
     * @return
     */
    public List<TjQuota> findQuotaMetadataList(HealthBusiness healthBusiness) {
        String sql = "";
        RowMapper rowMapper = null;
        if(healthBusiness != null) {
            sql = "select * from tj_quota tj where tj.quota_type = " + healthBusiness.getId();
            rowMapper = (RowMapper) BeanPropertyRowMapper.newInstance(TjQuota.class);
            return this.jdbcTemplate.query(sql, rowMapper);
        }else {
            return null;
        }
    }

    /**
     * 递归获取指标分类包含的子集分类和指标
     * @param healthBusiness
     * @return
     */
    public Map<String, Object> getTreeMap(HealthBusiness healthBusiness, int level, String filters) {
        Map<String, Object> masterMap = new HashMap<String, Object>();
        if(healthBusiness != null) {
            /**
             * 处理自身数据
             */
            masterMap.put("level", level);
            masterMap.put("id", healthBusiness.getId());
            masterMap.put("name", healthBusiness.getName());
            masterMap.put("parent_id", healthBusiness.getParentId());
            masterMap.put("code", healthBusiness.getCode());
            masterMap.put("note", healthBusiness.getNote());
            List<TjQuota> tList = findQuotaMetadataList(healthBusiness);
            if (level != 0) {
                if (tList != null) {
                    List<Map<String, Object>> detailList = new ArrayList<Map<String, Object>>();
                    for (TjQuota tjQuota : tList) {
                        Map<String, Object> detailMap = new HashMap<String, Object>();
                        detailMap.put("level", level + 1);
                        detailMap.put("id", tjQuota.getId());
                        detailMap.put("name", tjQuota.getName());
                        detailMap.put("code", tjQuota.getCode());
                        detailMap.put("status", tjQuota.getStatus());
                        detailMap.put("data_level", tjQuota.getDataLevel());
                        detailMap.put("quota_type", tjQuota.getQuotaType());
                        detailList.add(detailMap);
                    }
                    masterMap.put("detailList", detailList);
                } else {
                    masterMap.put("detailList", null);
                }
            }
            /**
             * 处理子集数据
             */
            List<HealthBusiness> hList = findHealthBusinessList(healthBusiness.getId(), filters);
            if(hList != null) {
                List<Map<String, Object>> childList = new ArrayList<Map<String, Object>>();
                for(HealthBusiness healthBusiness1: hList) {
                    childList.add(getTreeMap(healthBusiness1, level + 1, filters));
                }
                masterMap.put("child", childList);
            }
        }
        return masterMap;
    }

    /**
     * 综合查询档案数据列表树
     * @param filters
     * @return
     */
    public List<Map<String, Object>> getMetadataList(String filters) {
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        Map<String, Object> baseMap = new HashMap<String, Object>();
        List<Map<String, String>> baseList = new ArrayList<Map<String, String>>();
        /**
         * 处理基本数据
         */
        Map<String, String> baseMap1 = new HashMap<String, String>();
        baseMap1.put("code", "event_date");
        baseMap1.put("name", "时间");
        baseList.add(baseMap1);
        Map<String, String> baseMap2 = new HashMap<String, String>();
        baseMap2.put("code", "org_name");
        baseMap2.put("name", "机构名称");
        baseList.add(baseMap2);
        Map<String, String> baseMap3 = new HashMap<String, String>();
        baseMap3.put("code", "org_code");
        baseMap3.put("name", "机构编号");
        baseList.add(baseMap3);
        Map<String, String> baseMap4 = new HashMap<String, String>();
        baseMap4.put("code", "demographic_id");
        baseMap4.put("name", "病人身份证号码");
        baseList.add(baseMap4);
        Map<String, String> baseMap5 = new HashMap<String, String>();
        baseMap5.put("code", "patient_name");
        baseMap5.put("name", "病人姓名");
        baseList.add(baseMap5);
        Map<String, String> baseMap6 = new HashMap<String, String>();
        baseMap6.put("code", "event_type");
        baseMap6.put("name", "事件类型");
        baseList.add(baseMap6);
        baseMap.put("level", "0");
        baseMap.put("baseInfo", baseList);
        resultList.add(baseMap);
        List<RsResources> rrList = findFileMasterList(filters);
        if(rrList != null) {
            for(RsResources rsResources : rrList) {
                Map<String, Object> masterMap = new HashMap<String, Object>();
                masterMap.put("code", rsResources.getCode());
                masterMap.put("name", rsResources.getName());
                masterMap.put("level", "1");
                List<RsMetadata> rmList = findFileMetadataList(rsResources);
                if(rmList != null) {
                    List<Map<String, Object>> metadataList = new ArrayList<Map<String, Object>>();
                    for(RsMetadata rsMetadata : rmList) {
                        Map<String, Object> metadataMap = new HashMap<String, Object>();
                        metadataMap.put("level", "2");
                        metadataMap.put("code", rsMetadata.getId());
                        metadataMap.put("name", rsMetadata.getName());
                        metadataMap.put("metaDataStdCode", rsMetadata.getStdCode());
                        metadataMap.put("dictCode", rsMetadata.getDictCode());
                        metadataMap.put("description", rsMetadata.getDescription());
                        metadataMap.put("groupData", "");
                        metadataMap.put("groupType", "");
                        metadataList.add(metadataMap);
                    }
                    masterMap.put("metaDataList", metadataList);
                }else {
                    masterMap.put("metaDataList", null);
                }
                resultList.add(masterMap);
            }
        }
        return resultList;
    }

    /**
     * 综合查询档案数据检索
     * @return
     */
    public List<Map<String, Object>> searchMetadataData(String resourcesCode, String metaData, String orgCode, String appId, String queryCondition, Integer page, Integer size) throws Exception{
        Pattern pattern = Pattern.compile("\\[.+?\\]");
        if(resourcesCode != null) {
            Matcher rcMatcher = pattern.matcher(resourcesCode);
            if(!rcMatcher.find()) {
                return null;
            }
        }
        if(metaData != null) {
            Matcher mdMatcher = pattern.matcher(metaData);
            if(!mdMatcher.find()) {
                metaData = "";
            }
        }else {
            metaData = "";
        }
        if(queryCondition == null) {
            queryCondition = "[]";
        }else {
            Matcher qcMatcher = pattern.matcher(queryCondition);
            if (!qcMatcher.find()) {
                queryCondition = "[]";
            }else {
                if (!queryCondition.contains("{") || !queryCondition.contains("}")) {
                    queryCondition = "[]";
                }
            }
        }
        Envelop envelop = resourcesQueryService.getCustomizeData(resourcesCode, metaData, orgCode, appId, queryCondition, page, size);
        return envelop.getDetailModelList();
    }

    /**
     * 综合查询指标统计列表树
     * @param filters
     * @return
     */
    public List<Map<String, Object>> getQuotaList(String filters) {
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        /**
         * 获取最上级目录
         */
        List<HealthBusiness> parentList = findHealthBusinessList(0, filters);
        if(parentList != null) {
            for(HealthBusiness healthBusiness : parentList) {
                Map<String, Object> childMap = getTreeMap(healthBusiness, 0, filters);
                if (filters != null && !filters.equals("")) {
                    if(((List<String>) childMap.get("child")).size() > 0) {
                        resultList.add(childMap);
                    }
                }else {
                    resultList.add(childMap);
                }
            }
        }
        if(filters != null && !filters.equals("")) {
            for(Map<String, Object> tempMap : resultList) {

            }
        }
        return resultList;
    }
}
