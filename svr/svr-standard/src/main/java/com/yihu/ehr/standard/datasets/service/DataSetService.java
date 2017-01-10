package com.yihu.ehr.standard.datasets.service;


import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.util.BaseHbmService;
import com.yihu.ehr.util.CDAVersionUtil;
import org.apache.commons.lang.StringUtils;
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
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


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



    @Transactional(propagation = Propagation.REQUIRED)
    public void batchInsertDictsAndEntry(List<Map<String, Object>> models, String version) throws SQLException, InstantiationException, IllegalAccessException {

        String sql = "INSERT INTO "+ CDAVersionUtil.getMetaDataTableName(version) +
                "(dataset_id, code, inner_code, name, type, format, dict_id, definition,nullable, column_type, " +
                "column_name, column_length, primary_key, hash) VALUES ";
        int i = 1;
        int j = 0;
        StringBuilder sb = new StringBuilder(sql);
        String tmp;
        int nullable;
        int primaryKey;
        for(Map<String, Object> map: models){
            long dataSetId = add(map, version);
            for(Map<String, Object> meta: (List<Map<String, Object>>) map.get("children")){
                sb.append("('"+ dataSetId +"',");
                sb.append("'"+ meta.get("code") +"',");
                sb.append("'"+ meta.get("innerCode") +"',");
                sb.append("'"+ meta.get("name") +"',");
                sb.append("'"+ meta.get("type") +"',");
                sb.append("'"+ meta.get("format") +"',");
                sb.append("'"+ (StringUtils.isEmpty((String) meta.get("dictId")) ? 0 : meta.get("dictId")) +"',");
                sb.append("'"+ meta.get("definition") +"',");
                tmp = (String) meta.get("nullable");
                nullable = StringUtils.isEmpty(tmp) ? 0: Integer.parseInt(tmp);
                sb.append(""+ nullable +",");
                sb.append("'"+ meta.get("columnType") +"',");
                sb.append("'"+ meta.get("columnName") +"',");
                sb.append("'"+ meta.get("columnLength") +"',");
                tmp = (String) meta.get("primaryKey");
                primaryKey = StringUtils.isEmpty(tmp) ? 0: Integer.parseInt(tmp);
                sb.append(""+ primaryKey +",");
                sb.append("'"+ getMetaHash(meta, dataSetId, nullable, primaryKey) +"')");
                j = 1;
                if(i%100==0){
                    currentSession().createSQLQuery(sb.toString()).executeUpdate();
                    sb = new StringBuilder(sql) ;
                    j = 0;
                }
                else
                    sb.append(",");
                i++;
            }
        }

        if(j==1)
            currentSession().createSQLQuery(sb.substring(0, sb.length()-1)).executeUpdate();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public long add(Map<String, Object> dataSet, String version) throws IllegalAccessException, InstantiationException {
        BaseDataSet data = (BaseDataSet) getServiceEntity(version).newInstance();
        data.setName(String.valueOf(dataSet.get("name")));
        data.setCode(String.valueOf(dataSet.get("code")));
        data.setReference(String.valueOf(dataSet.get("referenceCode")));
        data.setStdVersion(version);
        data.setSummary(String.valueOf(dataSet.get("summary")));

        save(data);
        return data.getId();
        //这里涉及到动态查询和对象拷贝，导致效率不高，现在用原始的jdbc查询
//        String sql =
//                "INSERT INTO " + getTaleName(version) +
//                        "(code, name, ref_standard, std_version, summary, hash, document_id, lang, catalog, publisher) " +
//                        "VALUES (?,?,?,?,?,?,?,?,?,?)";
//
//        KeyHolder keyHolder = new GeneratedKeyHolder();
//        jdbcTemplate.update(
//                con -> {
//                    PreparedStatement ps = jdbcTemplate.getDataSource()
//                            .getConnection().prepareStatement(sql,
//                                    new String[]{"id","code","name","refStandard","version","summary","hashCode","documentId", "lang","catalog","publisher"});
//                    ps.setString(1, String.valueOf(dataSet.get("code")));
//                    ps.setString(2, String.valueOf(dataSet.get("name")));
//                    ps.setString(3, String.valueOf(dataSet.get("referenceId")));
//                    ps.setString(4, version);
//                    ps.setString(5, String.valueOf(dataSet.get("summary")));
//                    ps.setInt(6, getDataHash(dataSet, version));
//                    ps.setLong(7, 0);
//                    ps.setInt(8, 0);
//                    ps.setInt(9, 0);
//                    ps.setInt(10, 0);
//                    return ps;
//                }, keyHolder);
//        return keyHolder.getKey().intValue();
    }

    public int getDataHash(Map<String, Object> model, String version) {
        return Objects.hash(0, 0, 0, model.get("referenceId"),
                0, version, model.get("code"), model.get("name"), model.get("summary"));
    }

    public int getMetaHash(Map<String, Object> model, long dataSetId, int nullable, int primaryKey) {
        return  Objects.hash(dataSetId, model.get("dict_id"), model.get("code"), model.get("definition"), model.get("format"), model.get("inner_code"),
                model.get("name"),model.get("type"),model.get("columnLength"),model.get("columnType"), nullable, primaryKey);
    }
}