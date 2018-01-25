package com.yihu.ehr.resource.dao;


import com.yihu.ehr.resource.model.DtoResourceMetadata;
import com.yihu.ehr.resource.model.RsMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


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
        String sql = "SELECT m.id, m.domain, m.name, m.std_code, m.display_code, m.column_type, m.null_able, m.dict_code, m.description, m.valid, a.group_type, a.group_data " +
                "FROM rs_resource_metadata a, rs_resource b, rs_metadata m " +
                "WHERE a.resources_id = b.id " +
                "AND a.metadata_id = m.id " +
                "AND b.code = '" + resourcesCode + "'";
        RowMapper rowMapper = BeanPropertyRowMapper.newInstance(DtoResourceMetadata.class);
        return jdbcTemplate.query(sql, rowMapper);
    }

    /**
     * 获取某资源数据元(根据资源数据元id过滤)
     */
    public List<DtoResourceMetadata> getAuthResourceMetadata(String rsMetadataIds) throws Exception {
        String sql = "SELECT m.id, m.domain, m.name, m.std_code, m.display_code, m.column_type, m.null_able, m.dict_code, m.description, m.valid, a.group_type, a.group_data " +
                "FROM rs_resource_metadata a, rs_metadata m " +
                "WHERE a.id IN (" + rsMetadataIds + ") " +
                "AND a.metadata_id = m.id ";
        RowMapper rowMapper = BeanPropertyRowMapper.newInstance(DtoResourceMetadata.class);
        return jdbcTemplate.query(sql, rowMapper);
    }

    /**
     * 为其他平台标准数据元转换新增的方法
     * @param rsMetadataIds
     * @return
     * @throws Exception
     */
    public List<DtoResourceMetadata> getRsMetadataByIds(String rsMetadataIds) throws Exception {
        String sql = "SELECT m.id, m.std_code, m.column_type, m.dict_code " +
                "FROM rs_metadata m " +
                "WHERE m.id IN (" + rsMetadataIds + ")";
        RowMapper rowMapper = BeanPropertyRowMapper.newInstance(RsMetadata.class);
        return jdbcTemplate.query(sql, rowMapper);
    }

    public List<Map<String, Object>> getMetaData(List<String> idsList){
        StringBuilder builder = new StringBuilder();
        for (String id : idsList){
            builder.append("'");
            builder.append(id);
            builder.append("',");
        }
        String ids = "";
        if (builder.length() > 1){
            ids = builder.substring(0, builder.length()-1);
        }
        String sql ="SELECT ID, NAME FROM rs_metadata WHERE ID in(" + ids + ")";
        List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
        return list;
    }

}
