package com.yihu.ehr.standard.dict.service;


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
            throw new ApiException(ErrorCode.NotFoundStdDictView);
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


    //TODO: 从excel导入字典、字典项
}
