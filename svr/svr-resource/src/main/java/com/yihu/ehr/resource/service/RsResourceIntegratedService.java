package com.yihu.ehr.resource.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.entity.quota.TjQuota;
import com.yihu.ehr.entity.report.QuotaCategory;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.ResourceBrowseMetadataDao;
import com.yihu.ehr.resource.dao.RsResourceDao;
import com.yihu.ehr.resource.model.*;
import com.yihu.ehr.util.rest.Envelop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Sxy on 2016/08/01.
 */
@Service
@Transactional
public class RsResourceIntegratedService extends BaseJpaService<RsResource, RsResourceDao> {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ResourceBrowseMetadataDao resourceBrowseMetadataDao;
    @Autowired
    private ResourceBrowseService resourceBrowseService;
    @Autowired
    private RsRolesResourceGrantService rsRolesResourceGrantService;
    @Autowired
    private RsRolesResourceMetadataGrantService rsRolesResourceMetadataGrantService;
    @Autowired
    private RsDictionaryService rsDictionaryService;


    /**
     * 获取档案数据主体列表
     * @return
     */
    public List<RsResource> findFileMasterList(String ids, String filters) {
        String sql = "";
        if (filters != null) {
            if(ids != null) {
                sql = "SELECT rr.id, rr.code, rr.name, rr.rs_interface, rr.grant_type FROM rs_resource rr WHERE ((rr.category_id IN (SELECT id FROM rs_resource_category WHERE code = 'standard') " +
                        "AND (rr.rs_interface = 'getEhrCenter' AND rr.id IN (" + ids + "))) " +
                        "OR ((rr.category_id IN (SELECT id FROM rs_resource_category WHERE code = 'standard') AND rr.rs_interface = 'getEhrCenter' AND rr.grant_type = '0'))) " +
                        "AND rr.name like " + "'%" + filters + "%'";
            }else {
                sql = "SELECT rr.id, rr.code, rr.name, rr.rs_interface, rr.grant_type FROM rs_resource rr WHERE rr.category_id IN (SELECT id FROM rs_resource_category WHERE code = 'standard') " +
                        "AND rr.rs_interface = 'getEhrCenter' " +
                        "AND rr.name like " + "'%" + filters + "%'";
            }
        } else {
            if(ids != null) {
                sql = "SELECT rr.id, rr.code, rr.name, rr.rs_interface, rr.grant_type FROM rs_resource rr WHERE ((rr.category_id IN (SELECT id FROM rs_resource_category WHERE code = 'standard') " +
                        "AND (rr.rs_interface = 'getEhrCenter' AND rr.id IN (" + ids + "))) " +
                        "OR ((rr.category_id IN (SELECT id FROM rs_resource_category WHERE code = 'standard') AND rr.rs_interface = 'getEhrCenter' AND rr.grant_type = '0')))";
            }else {
                sql = "SELECT rr.id, rr.code, rr.name, rr.rs_interface, rr.grant_type FROM rs_resource rr WHERE rr.category_id IN (SELECT id FROM rs_resource_category WHERE code = 'standard') " +
                        "AND rr.rs_interface = 'getEhrCenter'";
            }
        }
        RowMapper rowMapper = BeanPropertyRowMapper.newInstance(RsResource.class);
        return jdbcTemplate.query(sql, rowMapper);
    }

    /**
     * 根据档案数据主体获取数据元列表
     * @param rsResource
     * @param roleId
     * @return
     */
    public List<RsMetadata> findFileMetadataList(RsResource rsResource, String roleId) throws Exception{
        List<DtoResourceMetadata> metadataList;
        Set<String> rsMetadataIdSet = new HashSet<String>();
        String grantType = rsResource.getGrantType();
        //获取数据元信息
        if(grantType.equals("1") && !roleId.equals("*")) {
            List<String> roleIdList = objectMapper.readValue(roleId, List.class);
            for(String id : roleIdList) {
                RsRolesResource rsRolesResource = rsRolesResourceGrantService.findByResourceIdAndRolesId(rsResource.getId(), id);
                if(rsRolesResource != null) {
                    List<RsRolesResourceMetadata> rsRolesResourceMetadataList = rsRolesResourceMetadataGrantService.findByRolesResourceIdAndValid(rsRolesResource.getId(), "1");
                    if(rsRolesResourceMetadataList != null) {
                        for (RsRolesResourceMetadata rsRolesResourceMetadata : rsRolesResourceMetadataList) {
                            rsMetadataIdSet.add(rsRolesResourceMetadata.getResourceMetadataId());
                        }
                    }
                }
            }
            if(rsMetadataIdSet.size() > 0) {
                StringBuilder builder = new StringBuilder();
                for (String id : rsMetadataIdSet) {
                    builder.append("'");
                    builder.append(id);
                    builder.append("',");
                }
                String rsMetadataIds = builder.toString();
                metadataList = resourceBrowseMetadataDao.getAuthResourceMetadata(rsMetadataIds.substring(0, rsMetadataIds.length() - 1));
            }else {
                metadataList = null;
            }
        } else{
            metadataList = resourceBrowseMetadataDao.getAllResourceMetadata(rsResource.getCode());
        }
        if(metadataList != null && metadataList.size() > 0) {
            String metadataIds = "";
            for(DtoResourceMetadata dtoResourceMetadata : metadataList){
                metadataIds += "'" + dtoResourceMetadata.getId() + "'" + ",";
            }
            String sql = "";
            sql = "select * from rs_metadata rm where rm.id in (" + metadataIds.substring(0, metadataIds.length() - 1) + ")";
            RowMapper rowMapper = BeanPropertyRowMapper.newInstance(RsMetadata.class);
            return jdbcTemplate.query(sql, rowMapper);
        }else {
            return null;
        }
    }

    /**
     * 根据parentId获取指标分类主体列表
     * @return
     */
    public List<QuotaCategory> findQuotaCategoryList(int parentId, String filters) {
        String sql;
        if(parentId != 0 && filters != null) {
            sql = "select * from tj_quota_category where parent_id = " + parentId + " AND name like " + "'%" + filters + "%'";
        }else {
            sql = "select * from tj_quota_category where parent_id = " + parentId;
        }
        RowMapper rowMapper =  BeanPropertyRowMapper.newInstance(QuotaCategory.class);
        return jdbcTemplate.query(sql, rowMapper);
    }

    /**
     * 根据指标分类获取指标
     * @param quotaCategory
     * @return
     */
    public List<TjQuota> findQuotaMetadataList(QuotaCategory quotaCategory) {
        String sql = "";
        if(quotaCategory != null) {
            sql = "select * from tj_quota tj where tj.quota_type = " + quotaCategory.getId();
            RowMapper rowMapper = BeanPropertyRowMapper.newInstance(TjQuota.class);
            return jdbcTemplate.query(sql, rowMapper);
        }else {
            return null;
        }
    }

    /**
     * 递归获取指标分类包含的子集分类和指标
     * @param quotaCategory
     * @return
     */
    public Map<String, Object> getTreeMap(QuotaCategory quotaCategory, int level, String filters) {
        Map<String, Object> masterMap = new HashMap<String, Object>();
        if(quotaCategory != null) {
            /**
             * 处理自身数据
             */
            masterMap.put("level", level);
            masterMap.put("id", quotaCategory.getId());
            masterMap.put("name", quotaCategory.getName());
            masterMap.put("parent_id", quotaCategory.getParentId());
            masterMap.put("code", quotaCategory.getCode());
            masterMap.put("note", quotaCategory.getNote());
            List<TjQuota> tList = findQuotaMetadataList(quotaCategory);
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
            List<QuotaCategory> hList = findQuotaCategoryList(quotaCategory.getId(), filters);
            if(hList != null) {
                List<Map<String, Object>> childList = new ArrayList<Map<String, Object>>();
                for(QuotaCategory quotaCategory1: hList) {
                    childList.add(getTreeMap(quotaCategory1, level + 1, filters));
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
    public Envelop getMetadataList(String userResource, String roleId, String filters) throws Exception{
        Envelop envelop = new Envelop();
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        Map<String, Object> baseMap = new HashMap<String, Object>();
        List<Map<String, String>> baseList = new ArrayList<Map<String, String>>();
        //处理基本数据
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
        List<RsResource> rrList;
        if(userResource.equals("*")) {
            rrList = findFileMasterList(null, filters);
        }else {
            //授权资源
            List<String> userResourceList = objectMapper.readValue(userResource, List.class);
            StringBuilder builder = new StringBuilder();
            for(String id : userResourceList) {
                builder.append("'");
                builder.append(id);
                builder.append("',");
            }
            String ids = builder.toString();
            if(StringUtils.isEmpty(ids)) {
                rrList = findFileMasterList("''", filters);
            }else {
                rrList = findFileMasterList(ids.substring(0, ids.length() -1), filters);
            }
        }
        if(rrList != null) {
            for(RsResource rsResources : rrList) {
                Map<String, Object> masterMap = new HashMap<String, Object>();
                masterMap.put("code", rsResources.getCode());
                masterMap.put("name", rsResources.getName());
                masterMap.put("level", "1");
                List<RsMetadata> rmList = findFileMetadataList(rsResources, roleId);
                if(rmList != null) {
                    List<Map<String, Object>> metadataList = new ArrayList<Map<String, Object>>();
                    for(RsMetadata rsMetadata : rmList) {
                        Map<String, Object> metadataMap = new HashMap<String, Object>();
                        metadataMap.put("level", "2");
                        metadataMap.put("code", rsMetadata.getId());
                        metadataMap.put("name", rsMetadata.getName());
                        metadataMap.put("stdCode", rsMetadata.getStdCode());
                        String dictCode = rsMetadata.getDictCode();
                        metadataMap.put("dictCode", rsMetadata.getDictCode());
                        if(!StringUtils.isEmpty(dictCode)){
                            if (dictCode.equals("DATECONDITION")) {
                                metadataMap.put("dictName", "时间");
                            }else {
                                RsDictionary rsDictionary = rsDictionaryService.findByCode(rsMetadata.getDictCode());
                                if(rsDictionary != null) {
                                    metadataMap.put("dictName", rsDictionary.getName());
                                }
                            }
                        }
                        metadataMap.put("description", rsMetadata.getDescription());
                        metadataMap.put("groupData", "");
                        metadataMap.put("groupType", "");
                        metadataList.add(metadataMap);
                    }
                    masterMap.put("metaDataList", metadataList);
                    resultList.add(masterMap);
                }
            }
        }
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(resultList);
        return envelop;
    }

    /**
     * 综合查询档案数据检索
     * @return
     */
    public Envelop searchMetadataData(String resourcesCode, String metaData, String orgCode, String areaCode, String queryCondition, Integer page, Integer size) throws Exception{
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
        return resourceBrowseService.getCustomizeData(resourcesCode, metaData, orgCode, areaCode, queryCondition, page, size);
    }

    /**
     * 综合查询指标统计列表树
     * @param filters
     * @return
     */
    public Envelop getQuotaList(String filters) {
        Envelop envelop = new Envelop();
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        /**
         * 获取最上级目录
         */
        List<QuotaCategory> parentList = findQuotaCategoryList(0, filters);
        if(parentList != null) {
            for(QuotaCategory quotaCategory : parentList) {
                Map<String, Object> childMap = getTreeMap(quotaCategory, 0, filters);
                if (filters != null && !filters.equals("")) {
                    if(((List<String>) childMap.get("child")).size() > 0) {
                        resultList.add(childMap);
                    }
                }else {
                    resultList.add(childMap);
                }
            }
        }
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(resultList);
        return envelop;
    }

}
