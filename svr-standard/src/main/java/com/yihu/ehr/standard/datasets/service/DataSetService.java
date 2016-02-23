package com.yihu.ehr.standard.datasets.service;


import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.query.BaseHbmService;
import com.yihu.ehr.util.CDAVersionUtil;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


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
                "(code, name, ref_standard, std_version, summary) " +
                "VALUES (:code, :name, :refStandard, :version, :summary)";
        Query query = currentSession().createSQLQuery(sql);
        query.setParameter("code", dataSet.getCode());
        query.setParameter("name", dataSet.getName());
        query.setParameter("refStandard", dataSet.getReference());
        query.setParameter("version", dataSet.getStdVersion());
        query.setParameter("summary", dataSet.getSummary());
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

    //TODO: 从excel导入数据集、数据元

}