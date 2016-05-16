package com.yihu.ehr.resource.dao;


import com.yihu.ehr.resource.model.DtoResourceMetadata;
import com.yihu.ehr.resource.model.RsAppResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Created by hzp on 2016/5/14.
 */
@Service("resourcesMetadataQueryDao")
public class ResourcesMetadataQueryDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    /**
     * 通过AppId获取是否有资源权限
     */
    public RsAppResource loadAppResource(String appId) throws Exception
    {
        String sql = "select * from RS_APP_RESOURCE";
        RowMapper rowMapper = (RowMapper) BeanPropertyRowMapper.newInstance(RsAppResource.class);
        return (RsAppResource)this.jdbcTemplate.queryForObject(sql,rowMapper);
    }

    /**
     * 获取某资源所有数据元
     */
    public List<DtoResourceMetadata> getResourceMetadata(String resourcesCode) throws Exception
    {
        String sql = "select a.id,m.domain,m.name,m.code,m.STD_CODE,m.DISPLAY_CODE,m.COLUMN_TYPE,m.NULL_ABLE,m.DICT_CODE,m.DESCRIPTION,m.VALID,a.STATS_TYPE\n" +
                "from rs_resource_metadata a,rs_resources b,rs_metadata m \n" +
                "where a.RESOURCES_ID=b.ID \n" +
                "and a.METADATA_ID = m.id \n" +
                "and b.code='"+resourcesCode+"'";
        RowMapper rowMapper = (RowMapper) BeanPropertyRowMapper.newInstance(DtoResourceMetadata.class);
        return this.jdbcTemplate.query(sql, rowMapper);
    }

    /**
     * 获取某资源所有数据元
     */
    public List<DtoResourceMetadata> getResourceMetadata(String resourcesCode,String appResourceId) throws Exception
    {
        String sql = "select a.id,m.domain,m.name,m.code,m.STD_CODE,m.DISPLAY_CODE,m.COLUMN_TYPE,m.NULL_ABLE,m.DICT_CODE,m.DESCRIPTION,m.VALID,a.STATS_TYPE\n" +
                "from rs_resource_metadata a,rs_resources b,rs_metadata m,rs_app_resource_metadata n\n" +
                "where a.RESOURCES_ID=b.ID \n" +
                "and a.METADATA_ID = m.id\n" +
                "and a.id = n.RESOURCE_METADATA_ID\n" +
                "and b.code='"+resourcesCode+"'\n" +
                "and n.APP_RESOURCE_ID='"+appResourceId+"'";
        RowMapper rowMapper = (RowMapper) BeanPropertyRowMapper.newInstance(DtoResourceMetadata.class);
        return this.jdbcTemplate.query(sql, rowMapper);
    }
}
