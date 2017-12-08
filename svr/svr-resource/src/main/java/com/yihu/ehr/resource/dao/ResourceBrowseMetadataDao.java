package com.yihu.ehr.resource.dao;


import com.yihu.ehr.resource.model.DtoResourceMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Created by hzp on 2016/5/14.
 */
@Service
public class ResourceBrowseMetadataDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取某资源所有数据元
     */
    public List<DtoResourceMetadata> getAllResourceMetadata(String resourcesCode) throws Exception {
        String sql = "SELECT m.id, m.domain, m.name, m.STD_CODE, m.DISPLAY_CODE, m.COLUMN_TYPE, m.NULL_ABLE, m.DICT_CODE, m.DESCRIPTION, m.VALID, a.GROUP_TYPE, a.GROUP_DATA \n" +
                "FROM rs_resource_metadata a, rs_resource b, rs_metadata m \n" +
                "WHERE a.RESOURCES_ID = b.ID \n" +
                "AND a.METADATA_ID = m.id \n" +
                "AND b.code = '" + resourcesCode + "'";
        RowMapper rowMapper = BeanPropertyRowMapper.newInstance(DtoResourceMetadata.class);
        return jdbcTemplate.query(sql, rowMapper);
    }


    /**
     * 获取某资源数据元(根据资源数据元id过滤)
     */
    public List<DtoResourceMetadata> getAuthResourceMetadata(String rsMetadataIds) throws Exception {
        String sql = "SELECT m.id, m.domain, m.name, m.STD_CODE, m.DISPLAY_CODE, m.COLUMN_TYPE, m.NULL_ABLE, m.DICT_CODE, m.DESCRIPTION, m.VALID, a.GROUP_TYPE, a.GROUP_DATA " +
                "FROM rs_resource_metadata a, rs_metadata m " +
                "WHERE a.id IN (" + rsMetadataIds + ") " +
                "AND a.METADATA_ID = m.id ";
        RowMapper rowMapper = BeanPropertyRowMapper.newInstance(DtoResourceMetadata.class);
        return jdbcTemplate.query(sql, rowMapper);
    }
}
