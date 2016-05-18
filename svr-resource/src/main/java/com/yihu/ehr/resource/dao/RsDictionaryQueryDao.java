package com.yihu.ehr.resource.dao;


import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.resource.model.RsDictionary;
import com.yihu.ehr.util.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;


/**
 * @author linaz
 * @created 2016.05.17 16:33
 */
@Service("dictionaryQueryDao")
public class RsDictionaryQueryDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Value("${deploy.region}")
    Short deployRegion = 3502;

    public void batchInsertDictionaries(List<RsDictionary> dictionaries) {
        final List<RsDictionary> tempDictionaries = dictionaries;
        String sql = "insert into rs_dictionary(id,code,name,description) values(?,?,?,?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                String code = tempDictionaries.get(i).getCode();
                String name = tempDictionaries.get(i).getName();
                String description = tempDictionaries.get(i).getDescription();
                ps.setString(1, new ObjectId(deployRegion, BizObject.RsDictionary).toString());
                ps.setString(2, code);
                ps.setString(3, name);
                ps.setString(4, description);
            }
            public int getBatchSize() {
                return tempDictionaries.size();
            }
        });

    }

}
