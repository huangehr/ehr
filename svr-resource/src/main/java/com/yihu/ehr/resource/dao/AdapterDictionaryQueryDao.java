package com.yihu.ehr.resource.dao;


import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.resource.model.RsAdapterDictionary;
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
@Service("adapterDictionaryQueryDao")
public class AdapterDictionaryQueryDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Value("${deploy.region}")
    Short deployRegion = 3502;

    public void batchInsertAdapterDictionaries(RsAdapterDictionary[] adapterDictionaries) {
        final RsAdapterDictionary[] tempDictionaries = adapterDictionaries;
        String sql = "insert into rs_adapter_metadata(id,schema_id,dictCode,dictEntryCode,srcDictCode,srcDictEntryCode,srcDictEntryName) values(?,?,?,?,?,?,?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {

                String schemeId = tempDictionaries[i].getSchemeId();
                String dictCode = tempDictionaries[i].getDictCode();
                String dictEntryCode = tempDictionaries[i].getDictEntryCode();
                String srcDictCode = tempDictionaries[i].getSrcDictCode();
                String srcDictEntryCode = tempDictionaries[i].getSrcDictEntryCode();
                String srcDictEntryName = tempDictionaries[i].getSrcDictEntryName();

                ps.setString(1, new ObjectId(deployRegion, BizObject.RsAdapterDictionary).toString());
                ps.setString(2, schemeId);
                ps.setString(3, dictCode);
                ps.setString(4, dictEntryCode);
                ps.setString(5, srcDictCode);
                ps.setString(6, srcDictEntryCode);
                ps.setString(7, srcDictEntryName);
            }
            public int getBatchSize() {
                return tempDictionaries.length;
            }
        });

    }

}
