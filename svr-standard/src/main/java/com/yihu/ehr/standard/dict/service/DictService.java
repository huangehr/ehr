package com.yihu.ehr.standard.dict.service;


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
import java.util.Map;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.22
 */
@Service
@Transactional
public class DictService extends BaseHbmService<IDict> {
    private final static String ENTITY_PRE = "com.yihu.ehr.standard.dict.service.Dict";

    @Autowired
    private DictEntryService dictEntryService;

    public Class getServiceEntity(String version){
        try {
            return Class.forName(ENTITY_PRE + version);
        } catch (ClassNotFoundException e) {
            throw new ApiException(ErrorCode.NotFoundEntity, "字典版本", version);
        }
    }

    public String getTaleName(String version){

        return CDAVersionUtil.getDictTableName(version);
    }

    @Transactional(propagation= Propagation.REQUIRED)
    public boolean add(IDict dict){
        String sql =
                "INSERT INTO " + getTaleName(dict.getStdVersion()) +
                        "(code, name, author, base_dict, create_date, description, source, std_version, hash) " +
                        "VALUES (:code, :name, :author, :base_dict, :create_date, :description, :source, :std_version, :hash)";
        Query query = currentSession().createSQLQuery(sql);
        query.setParameter("code", dict.getCode());
        query.setParameter("name", dict.getName());
        query.setParameter("author", dict.getAuthor());
        query.setParameter("base_dict", dict.getBaseDict());
        query.setParameter("create_date", dict.getCreatedate());
        query.setParameter("description", dict.getDescription());
        query.setParameter("source", dict.getSourceId());
        query.setParameter("std_version", dict.getStdVersion());
        query.setParameter("hash", dict.getHashCode());
        return query.executeUpdate()>0;
    }


    @Transactional(propagation= Propagation.REQUIRED)
    public int removeDicts(Object[] ids, String version) {
        int row;
        if((row = delete(ids, getServiceEntity(version))) > 0){
            dictEntryService.deleteByDictId(ids, version);
        }
        return row;
    }


    public Map getDictMapByIds(String version, Long dataSetId, Long metaDataId) {
        Session session = currentSession();
        String dictTableName = CDAVersionUtil.getDictTableName(version);
        String metaTable = CDAVersionUtil.getMetaDataTableName(version);

        String sql =
                " select d.id as dictId, t.id as metaId, t.dataset_id from " + metaTable + " t " +
                        " left join " + dictTableName + " d on t.dict_id=d.id " +
                        " where d.id is not null and t.id = :metaDataId and t.dataset_id = :dataSetId ";

        Query query = session.createSQLQuery(sql);
        query.setParameter("metaDataId", metaDataId);
        query.setParameter("dataSetId", dataSetId);
        Object o = query.uniqueResult();
        Map map = null;
        if (o != null) {
            Object[] os = ((Object[]) o);
            map = new HashMap<>();
            map.put("dictId", os[0]);
            map.put("metaId", os[1]);
            map.put("datasetId", os[2]);
        }
        return map;
    }

    //TODO: 从excel导入字典、字典项
}
