//package com.yihu.ehr.resource.dao;
//
//import com.yihu.ehr.constants.BizObject;
//import com.yihu.ehr.resource.model.RsAdapterDictionary;
//import com.yihu.ehr.util.id.ObjectId;
//import org.apache.commons.dbcp2.BasicDataSource;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.jdbc.core.BatchPreparedStatementSetter;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Service;
//
//import java.sql.*;
//
//
///**
// * @author linaz
// * @created 2016.05.17 16:33
// */
//@Service("adapterDictionaryQueryDao")
//public class AdapterDictionaryQueryDao {
//
//    @Autowired
//    JdbcTemplate jdbcTemplate;
//
//    @Value("${deploy.region}")
//    Short deployRegion = 3502;
//
//    @Autowired
//    protected BasicDataSource basicDataSource;
//
//    public void batchInsertAdapterDictionaries(RsAdapterDictionary[] adapterDictionaries) throws SQLException {
//        Connection connection =  basicDataSource.getConnection();
//        connection.setAutoCommit(false);
//        Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
//                ResultSet.CONCUR_READ_ONLY);
//        for(int x = 0; x < adapterDictionaries.length; x++){
//
//            String schemeId = adapterDictionaries[x].getSchemeId();
//            String dictCode = adapterDictionaries[x].getDictCode();
//            String dictEntryCode = adapterDictionaries[x].getDictEntryCode();
//            String srcDictCode = adapterDictionaries[x].getSrcDictCode();
//            String srcDictEntryCode = adapterDictionaries[x].getSrcDictEntryCode();
//            String srcDictEntryName =adapterDictionaries[x].getSrcDictEntryName();
//            srcDictEntryName = srcDictEntryName == null ? srcDictEntryName:srcDictEntryName.replace("\'","\\\'");
//            stmt.execute("insert into rs_adapter_dictionary(id,scheme_id,dict_code,dict_entry_code,src_dict_code,src_dict_entry_code,src_dict_entry_name) values(" +
//                    "'"+new ObjectId(deployRegion, BizObject.RsAdapterDictionary).toString()+"'," +
//                    "'"+schemeId+"'," +
//                    "'"+dictCode+"'," +
//                    "'"+dictEntryCode+"'," +
//                    "'"+srcDictCode+"'," +
//                    "'"+srcDictEntryCode+"'," +
//                    "'"+srcDictEntryName+"')");
//        }
//        connection.commit();
//    }
//
//
//}
