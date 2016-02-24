package com.yihu.ehr.standard.datasets.service;

import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.query.BaseHbmService;
import com.yihu.ehr.standard.cdaversion.service.CDAVersion;
import com.yihu.ehr.util.CDAVersionUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 数据元管理接口实现。
 *
 * @author lincl
 * @version 1.0
 * @created 2016.2.22
 */
@Transactional
@Service
public class MetaDataService extends BaseHbmService<IMetaData>{

    private final static String ENTITY_PRE = "com.yihu.ehr.standard.datasets.service.MetaData";

    public String getTaleName(String version){
        return CDAVersionUtil.getMetaDataTableName(version);
    }

    public Class getServiceEntity(String version){
        try {
            return Class.forName(ENTITY_PRE + version);
        } catch (ClassNotFoundException e) {
            throw new ApiException(ErrorCode.NotFoundMetaDataView);
        }
    }


    public boolean isColumnValExsit(long dataSetId, String field, String val, Class entityClass){

        return isExistByFields(
                    new String[]{"dataSetId", field},
                    new Object[]{dataSetId, val},
                    entityClass
                );
    }

    public boolean saveMetaData(IMetaData metaData, String version) {
        Session session = currentSession();
        String sql = "insert into " + getTaleName(version) +
                    "(dataset_id, code, inner_code, name, type, format, dict_id, definition, nullable, column_type, column_name, column_length, primary_key, hash) " +
                    "values(:dataset_id, :code, :inner_code, :name, :type, :format, :dict_id, :definition, :nullable, :column_type, :column_name, :column_length, :primary_key, :hash)";

        Query query = session.createSQLQuery(sql);
        query.setLong("dataset_id", metaData.getDataSetId());
        query.setLong("dict_id", metaData.getDictId());
        query.setString("code", metaData.getCode());
        query.setString("inner_code", metaData.getInnerCode());
        query.setString("name", metaData.getName());
        query.setString("type", metaData.getType());
        query.setString("format", metaData.getFormat());
        query.setString("definition", metaData.getDefinition());
        query.setInteger("nullable", metaData.isNullable() ? 1 : 0);
        query.setString("column_type", metaData.getColumnType());
        query.setString("column_name", metaData.getColumnName());
        query.setString("column_length", metaData.getColumnLength());
        query.setInteger("primary_key", metaData.isPrimaryKey()?1:0);
        query.setInteger("hash", metaData.getHashCode());
        return query.executeUpdate()>0;
    }

    public boolean deleteByDataSetId(Object[] dataSetIds, String version){

        return deleteByField("dataSetId", dataSetIds, getServiceEntity(version)) > 0;
    }

}