package com.yihu.ehr.resource.dao;


import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.resource.model.RsAdapterMetadata;
import com.yihu.ehr.util.id.ObjectId;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


/**
 * @author linaz
 * @created 2016.05.17 16:33
 */
@Service("adapterMetadataQueryDao")
public class AdapterMetadataQueryDao {

    @Autowired
    protected BasicDataSource basicDataSource;

    @Value("${deploy.region}")
    Short deployRegion = 3502;

    public void batchAdapterMetadata(RsAdapterMetadata[] adapterMetadata) throws Exception {
        Connection connection =  basicDataSource.getConnection();
        connection.setAutoCommit(false);
        Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        for(int i = 0; i < adapterMetadata.length; i++){
            String schemaId = adapterMetadata[i].getSchemaId();
            String metadataId = adapterMetadata[i].getMetadataId();
            String srcDataSetCode = adapterMetadata[i].getSrcDatasetCode();
            String srcMetadataCode = adapterMetadata[i].getSrcMetadataCode();
            String srcMetadataName = adapterMetadata[i].getSrcMetadataName();
            String metadataDomain = adapterMetadata[i].getMetadataDomain();
            srcMetadataName = srcMetadataName == null ? srcMetadataName:srcMetadataName.replace("\'","\\\'");
            stmt.execute("insert into rs_adapter_metadata(id,schema_id,metadata_id,src_dataset_code,src_metadata_code,src_metadata_name,metadata_domain) values(" +
                    "'"+new ObjectId(deployRegion, BizObject.RsAdapterDictionary).toString()+"'," +
                    "'"+schemaId+"'," +
                    "'"+metadataId+"'," +
                    "'"+srcDataSetCode+"'," +
                    "'"+srcMetadataCode+"'," +
                    "'"+srcMetadataName+"'," +
                    "'"+metadataDomain+"')");
        }
        connection.commit();
    }

}
