package com.yihu.ehr.resource.service;


import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.intf.ResourcesCategoryDao;
import com.yihu.ehr.resource.dao.intf.ResourcesDao;
import com.yihu.ehr.resource.model.RsCategory;
import com.yihu.ehr.resource.model.RsMetadata;
import com.yihu.ehr.resource.model.RsResources;
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


/**
 * Created by Sxy on 2016/08/01.
 */
@Service
@Transactional
public class ResourcesCustomizeListService extends BaseJpaService<RsCategory,ResourcesCategoryDao> {

    @Autowired
    private ResourcesCategoryDao rsCategoryDao;
    @Autowired
    private ResourcesDao rsDao;
    @Autowired
    private JdbcTemplate jdbcTemplate;

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
     * 获取结果集
     * @param filters
     * @return
     */
    public List<Map<String, Object>> getCustomizeList(String filters) {
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        List<RsResources> rrList = findMasterList();
        if(rrList != null) {
            for(RsResources rsResources : rrList) {
                Map<String, Object> masterMap = new HashMap<String, Object>();
                masterMap.put("masterCode", rsResources.getCode());
                masterMap.put("masterName", rsResources.getName());
                List<RsMetadata> rmList = findMetadataList(rsResources);
                if(rmList != null) {
                    List<Map<String, Object>> metadataList = new ArrayList<Map<String, Object>>();
                    for(RsMetadata rsMetadata : rmList) {
                        Map<String, Object> metadataMap = new HashMap<String, Object>();
                        metadataMap.put("metaDataId", rsMetadata.getId());
                        metadataMap.put("metaDataName", rsMetadata.getName());
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

}
