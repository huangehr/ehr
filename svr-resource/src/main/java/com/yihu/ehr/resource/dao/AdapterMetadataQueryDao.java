package com.yihu.ehr.resource.dao;


import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.resource.model.RsAdapterMetadata;
import com.yihu.ehr.util.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.SQLException;


/**
 * @author linaz
 * @created 2016.05.17 16:33
 */
@Service("adapterMetadataQueryDao")
public class AdapterMetadataQueryDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Value("${deploy.region}")
    Short deployRegion = 3502;

    public void batchAdapterMetadata(RsAdapterMetadata[] adapterMetadata) {
        final RsAdapterMetadata[] tempDictionaries = adapterMetadata;
        String sql = "insert into rs_adapter_metadata(id,schema_id,metadata_id,src_dataset_code,src_metadata_code,src_metadata_name,metadata_domain) values(?,?,?,?,?,?,?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                String schemaId = tempDictionaries[i].getSchemaId();
                String metadataId = tempDictionaries[i].getMetadataId();
                String srcDataSetCode = tempDictionaries[i].getSrcDatasetCode();
                String srcMetadataCode = tempDictionaries[i].getSrcMetadataCode();
                String srcMetadataName = tempDictionaries[i].getSrcMetadataName();
                String metadataDomain = tempDictionaries[i].getMetadataDomain();

                ps.setString(1, new ObjectId(deployRegion, BizObject.RsAdapterMetadata).toString());
                ps.setString(2, schemaId);
                ps.setString(3, metadataId);
                ps.setString(4, srcDataSetCode);
                ps.setString(5, srcMetadataCode);
                ps.setString(6, srcMetadataName);
                ps.setString(7, metadataDomain);
            }
            public int getBatchSize() {
                return tempDictionaries.length;
            }
        });

    }

}
