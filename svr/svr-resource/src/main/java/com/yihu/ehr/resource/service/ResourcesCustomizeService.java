package com.yihu.ehr.resource.service;


import com.fasterxml.jackson.databind.ObjectMapper;
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
public class ResourcesCustomizeService extends BaseJpaService<RsResources, ResourcesDao> {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ResourcesQueryService resourcesQueryService;

    /**
     * 获取主体资源列表
     * @return
     */
    public List<RsResources> findMasterList() {
        String sql = "select rr.id, rr.code, rr.name, rr.rs_interface from rs_resources rr where rr.code in (select code from std_data_set_56395d75b854 where multi_record = 0)";
        RowMapper rowMapper = (RowMapper) BeanPropertyRowMapper.newInstance(RsResources.class);
        return this.jdbcTemplate.query(sql, rowMapper);
    }

    /**
     * 根据主体资源获取数据元列表
     * @param rsResources
     * @return
     */
    public List<RsMetadata> findMetadataList(RsResources rsResources) {
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
     * 获取自定义资源列表树
     * @param filters
     * @return
     */
    public List<Map<String, Object>> getCustomizeList(String filters) {
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        List<RsResources> rrList = findMasterList();
        if(rrList != null) {
            for(RsResources rsResources : rrList) {
                Map<String, Object> masterMap = new HashMap<String, Object>();
                masterMap.put("code", rsResources.getCode());
                masterMap.put("name", rsResources.getName());
                masterMap.put("level", "1");
                List<RsMetadata> rmList = findMetadataList(rsResources);
                if(rmList != null) {
                    List<Map<String, Object>> metadataList = new ArrayList<Map<String, Object>>();
                    for(RsMetadata rsMetadata : rmList) {
                        Map<String, Object> metadataMap = new HashMap<String, Object>();
                        metadataMap.put("level", "2");
                        metadataMap.put("code", rsMetadata.getId());
                        metadataMap.put("name", rsMetadata.getName());
                        metadataMap.put("metaDataStdCode", rsMetadata.getStdCode());
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
     * 获取自定义资源数据
     * @return
     */
    public List<Map<String, Object>> getCustomizeData(String resourcesCode, String metaData, String orgCode, String appId, String queryCondition, Integer page, Integer size) throws Exception{
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
}
