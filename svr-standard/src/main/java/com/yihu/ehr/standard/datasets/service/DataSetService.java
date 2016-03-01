package com.yihu.ehr.standard.datasets.service;


import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.query.BaseHbmService;
import com.yihu.ehr.util.CDAVersionUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
public class DataSetService extends BaseHbmService<IDataSet>{
    private final static String ENTITY_PRE = "com.yihu.ehr.standard.datasets.service.DataSet";

    @Autowired
    MetaDataService metaDataService;


    public Class getServiceEntity(String version){
        try {
            return Class.forName(ENTITY_PRE + version);
        } catch (ClassNotFoundException e) {
            throw new ApiException(ErrorCode.NotFoundDataSetView);
        }
    }

    public String getTaleName(String version){
        return CDAVersionUtil.getDataSetTableName(version);
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public void add(IDataSet dataSet){
        String sql =
                "INSERT INTO " + getTaleName(dataSet.getStdVersion()) +
                "(code, name, ref_standard, std_version, summary, hashCode, documentId, lang) " +
                "VALUES (:code, :name, :refStandard, :version, :summary, :hashCode, :documentId, :lang)";
        Query query = currentSession().createSQLQuery(sql);
        query.setParameter("code", dataSet.getCode());
        query.setParameter("name", dataSet.getName());
        query.setParameter("refStandard", dataSet.getReference());
        query.setParameter("version", dataSet.getStdVersion());
        query.setParameter("summary", dataSet.getSummary());
        query.setParameter("hashCode", dataSet.getHashCode());
        query.setParameter("documentId", dataSet.getDocumentId());
        query.setParameter("lang", dataSet.getLang());
        query.executeUpdate();
    }


    @Transactional(propagation= Propagation.REQUIRED)
    public int removeDataSet(Object[] ids, String version) {
        int row;
        if((row = delete(ids, getServiceEntity(version))) > 0){
            metaDataService.deleteByDataSetId(ids, version);
        }
        return row;
    }


    @Transactional(propagation = Propagation.SUPPORTS)
    public Map<Integer, String> getDataSetMapByIds(Object[] ids, String versionid) {
        Session session = currentSession();
        String dataSetTable = CDAVersionUtil.getDataSetTableName(versionid);
        String sql = "select id, name from " + dataSetTable;
        if (ids.length > 0)
            sql += " where id in(:ids) ";
        Query query = session.createSQLQuery(sql);
        if (ids.length > 0)
            query.setParameterList("ids", ids);
        List<Object> records = query.list();
        Map<Integer, String> rs = new HashMap<>();
        for (int i = 0; i < records.size(); ++i) {
            Object[] record = (Object[]) records.get(i);
            rs.put((Integer) record[0], (String) record[1]);
        }
        return rs;
    }

    //TODO: 从excel导入数据集、数据元

}