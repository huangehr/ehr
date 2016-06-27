package com.yihu.ehr.standard.datasets.service;


import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.util.BaseHbmService;
import com.yihu.ehr.util.CDAVersionUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 数据集管理器.
 *
 * @author lincl
 * @version 1.0
 * @created 2016.2.22
 */
@Transactional
@Service
public class DataSetService extends BaseHbmService<BaseDataSet> {
    private final static String ENTITY_PRE = "com.yihu.ehr.standard.datasets.service.DataSet";

    @Autowired
    MetaDataService metaDataService;

    @Autowired
    JdbcTemplate jdbcTemplate;


    public Class getServiceEntity(String version) {
        try {
            return Class.forName(ENTITY_PRE + version);
        } catch (ClassNotFoundException e) {
            throw new ApiException(ErrorCode.NotFoundEntity, "数据集版本", version);
        }
    }

    public String getTaleName(String version) {
        return CDAVersionUtil.getDataSetTableName(version);
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public boolean add(BaseDataSet dataSet, String version) {
        //这里涉及到动态查询和对象拷贝，导致效率不高，现在用原始的jdbc查询
        String sql =
                "INSERT INTO " + getTaleName(version) +
                        "(code, name, ref_standard, std_version, summary, hash, document_id, lang, catalog, publisher) " +
                        "VALUES (?,?,?,?,?,?,?,?,?,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                con -> {
                    PreparedStatement ps = jdbcTemplate.getDataSource()
                            .getConnection().prepareStatement(sql,
                                    new String[]{"id","code","name","refStandard","version","summary","hashCode","documentId", "lang","catalog","publisher"});
                    ps.setString(1, dataSet.getCode());
                    ps.setString(2, dataSet.getName());
                    ps.setString(3, dataSet.getReference());
                    ps.setString(4, dataSet.getCode());
                    ps.setString(5, dataSet.getStdVersion());
                    ps.setInt(6, dataSet.getHashCode());
                    ps.setLong(7, dataSet.getDocumentId());
                    ps.setInt(8, dataSet.getLang());
                    ps.setInt(9, dataSet.getCatalog());
                    ps.setInt(10,dataSet.getPublisher());
                    return ps;
                }, keyHolder);
        System.out.println("自动插入id============================" + keyHolder.getKey().intValue());
        String key = keyHolder.getKey().toString();


        return true;
//        return result > 0;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public int removeDataSet(Object[] ids, String version) {
        int row;
        if ((row = delete(ids, getServiceEntity(version))) > 0) {
            metaDataService.deleteByDataSetId(ids, version);
        }
        return row;
    }


    @Transactional(propagation = Propagation.SUPPORTS)
    public Map<Long, String> getDataSetMapByIds(Object[] ids, String versionid) {
        Session session = currentSession();
        String dataSetTable = CDAVersionUtil.getDataSetTableName(versionid);
        String sql = "select id, name from " + dataSetTable;
        if (ids.length > 0)
            sql += " where id in(:ids) ";
        Query query = session.createSQLQuery(sql);
        if (ids.length > 0)
            query.setParameterList("ids", ids);
        List<Object> records = query.list();
        Map<Long, String> rs = new HashMap<>();
        for (int i = 0; i < records.size(); ++i) {
            Object[] record = (Object[]) records.get(i);
            rs.put(((Integer) record[0]).longValue(), (String) record[1]);
        }
        return rs;
    }

}