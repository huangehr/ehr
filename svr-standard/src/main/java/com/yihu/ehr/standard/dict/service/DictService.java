package com.yihu.ehr.standard.dict.service;


import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.util.BaseHbmService;
import com.yihu.ehr.util.CDAVersionUtil;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.mapping.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.22
 */
@Service
@Transactional
public class DictService extends BaseHbmService<BaseDict> {
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
    public BaseDict getByCode(String code, String version){
        String hql = "select e from " + getServiceEntity(version).getName() + " e where code=:code";

        Query query = currentSession().createQuery(hql);
        query.setParameter("code", code);
        List<BaseDict> ls = query.list();
        return ls.size()>0? ls.get(0) : null;
    }


    @Transactional(propagation= Propagation.REQUIRED)
    public boolean add(BaseDict dict, String version){
        String sql =
                "INSERT INTO " + getTaleName(version) +
                        "(code, name, author, base_dict, create_date, description, source, std_version, hash) " +
                        "VALUES (:code, :name, :author, :base_dict, :create_date, :description, :source, :std_version, :hash)";
        Query query = currentSession().createSQLQuery(sql);
        query.setParameter("code", dict.getCode());
        query.setParameter("name", dict.getName());
        query.setParameter("author", dict.getAuthor());
        query.setParameter("base_dict", dict.getBaseDict());
        query.setParameter("create_date", dict.getCreateDate());
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


    public List<BaseDict> getChildrensByParentId(long baseDictId, String version) {
        Session session = currentSession();
        Class clz = getServiceEntity(version);
        String hql="";
        if(StringUtils.isEmpty(baseDictId)){
            hql += "from "+clz.getSimpleName()+" a where 1=1 and (a.baseDict is null or a.baseDict='')";
        }else{
            hql += "from "+clz.getSimpleName()+" a where 1=1 and a.baseDict =:baseDict";
        }
        Query query = session.createQuery(hql);
        if(!StringUtils.isEmpty(baseDictId)){
            query.setLong("baseDict", baseDictId);
        }
        return query.list();
    }

    public List<BaseDict> getCdaTypeExcludeSelfAndChildren(String childrenIds, String version) {
        Session session = currentSession();
        Class clz = getServiceEntity(version);

        String[] ids = childrenIds.split(",");

        Long[] idsL = new Long[ids.length];
        for (int i = 0; i < ids.length; i++) {
            idsL[i] = Long.parseLong(ids[i]);
        }

        String  hql = "from "+clz.getSimpleName()+" a where 1=1 and a.id not in (:ids)";
        Query query = session.createQuery(hql);
        query.setParameterList("ids",idsL);
        return query.list();
    }


    //TODO: 从excel导入字典、字典项

    @Transactional(propagation = Propagation.REQUIRED)
    public void batchInsertDictsAndEntry(List<Map<String, Object>> models,String version) throws Exception {
        //String dictTableName = CDAVersionUtil.getDictTableName(version);
        String dictEntryTableName = CDAVersionUtil.getDictEntryTableName(version);
        Session session = null;
        Transaction transaction =null;
        Connection connection = null;
        try {
            //String title = "INSERT INTO "+dictTableName+"(code, name, description,std_version,hash) VALUES ";
            //StringBuilder sql = new StringBuilder(title);
            //int i = 1;
            List<Map<String, Object>> dictEntry = new ArrayList<>();
            //插入字典

            Map<String,BaseDict> hashMap = new HashMap<>();
            for(Map<String, Object> map: models){
                Class entityClass = getServiceEntity(version);
                BaseDict dict =(BaseDict)entityClass.newInstance();
                dict.setCode(map.get("code")+"");
                dict.setName(map.get("name")+"");
                dict.setDescription(null2Space(map.get("description"))+"");
                dict.setStdVersion(version);
                dict.setCreateDate(new Date());
                dict.setHashCode(map.get("code").hashCode());
                this.save(dict);
                hashMap.put(map.get("code")+"",dict);
                dictEntry.addAll((List)map.get("children"));
                //sql.append("('"+  map.get("code") +"'");
                //sql.append(",'"+  map.get("name") +"'");
                //sql.append(",'"+  null2Space(map.get("description")) +"'");
                //sql.append(",'"+  version +"'");
                //sql.append(",'"+  map.get("code").hashCode() +"')");
                //if(i%100==0 || i == models.size()){
                //    session.createSQLQuery(sql.toString()).executeUpdate();
                //    sql = new StringBuilder(title) ;
                //}
                //else
                //    sql.append(",");
                //
                //
                //i++;
            }
            /**
             * 打破了完整性
             */
            //插入字典项
            session = currentSession().getSessionFactory().openSession();
            transaction = session.beginTransaction();

            String  title = "INSERT INTO "+dictEntryTableName+"(dict_id, code, value, description,hash) VALUES ";
            StringBuilder sql = new StringBuilder(title);
            int i = 1;
            BaseDict dict = null;
            for(Map<String, Object> map: dictEntry){
                if(dict==null||!map.get("dictCode").equals(dict.getCode())){
                    dict = hashMap.get(map.get("dictCode")+"");
                }
                sql.append("('"+ dict.getId() +"'");
                sql.append(",'" + map.get("code") +"'");
                sql.append(",'" + map.get("name") +"'");
                sql.append(",'"+  null2Space(map.get("description")) +"'");
                sql.append(",'"+  map.get("code").hashCode() +"')");
                if(i%100==0 || i == dictEntry.size()){
                    session.createSQLQuery(sql.toString()).executeUpdate();
                    sql = new StringBuilder(title) ;
                }
                else
                    sql.append(",");
                i++;
            }
            transaction.commit();
            session.close();
            //更新字典项关联字典字段
            //  sql = new StringBuilder("UPDATE "+dictEntryTableName+" e,"+dictTableName+" d SET e.dict_id =d.id WHERE e.dict_id=d.code AND (e.dict_id=0 OR e.dict_id IS NULL)");
            //jdbcTemplate.execute(sql.toString());
        } catch (Exception e){
            if(transaction!=null)transaction.rollback();
            if(session!=null) session.close();
            if(connection!=null)connection.close();
            throw e;
        }
    }
    public Object null2Space(Object str){
        return str==null? "" : str;
    }

    /**
     * 查询编码是否已存在， 返回已存在资源标准编码
     */
    public List codeExist(String[] codes,String version)
    {
        String dictTableName = CDAVersionUtil.getDictTableName(version);
        String sql = "SELECT DISTINCT code FROM "+dictTableName+" WHERE code in(:codes)";
        SQLQuery sqlQuery = currentSession().createSQLQuery(sql);
        sqlQuery.setParameterList("codes", codes);
        return sqlQuery.list();
    }
}
