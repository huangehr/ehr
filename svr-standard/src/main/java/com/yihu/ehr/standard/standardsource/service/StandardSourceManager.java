package com.yihu.ehr.standard.standardsource.service;

import com.yihu.ehr.standard.standardsource.model.StandardSourceModel;
import com.yihu.ehr.util.operator.DateUtil;
import com.yihu.ehr.util.operator.StringUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

/**
 * @author Lincl
 * @version 1.0
 * @created 2016.1.20
 */
@Service
@Transactional
public class StandardSourceManager {
    public static final String StandardSourceTableName = "std_standard_source";

    @Autowired
    XStandardSourceRepository standardSourceRepository;

    @PersistenceContext
    EntityManager entityManager;
    /**
     * 根据ID删除标准来源
     *
     * @param ids
     */
    public int deleteSource(List<String> ids) {
        int result = 0;
        try {
            if(ids==null || ids.size()==0)
                return 0;
            result = standardSourceRepository.deleteByIdIn(ids);
        } catch (Exception ex) {
            result = -1;
        }
        return result;
    }

    /**
     * 根据id组获取标准来源
     * @param ids
     */
    public StandardSource[] getSourceById(List<String> ids) {
        if(ids==null || ids.size()==0)
            return new StandardSource[0];
        return (StandardSource[]) standardSourceRepository.findByIdIn(ids).toArray();
    }

    /**
     * 查询所有的资源列表
     */
    public StandardSource[] getSourceList() {
        return getSourceByKey("");
    }

    /**
     * 根据Id获取标准来源
     * @param id
     */
    public StandardSource getSourceBySingleId(String id) {
        return  standardSourceRepository.findOne(id);
    }

    /**
     * 模糊查询（code、name）
     * @param strKey
     */
    public StandardSource[] getSourceByKey(String strKey) {
        Session session = currentSession();
        String strSql = "select  src from StandardSource src ";
        if (strKey != null && !strKey.equals("")) {
            strSql += " where code like :strKey or name like :strKey";
        }
        Query query = session.createQuery(strSql);
        if (strKey != null && !strKey.equals("")) {
            query.setParameter("strKey", "%" + strKey + "%");
        }
        return (StandardSource[])query.list().toArray();
    }

    /**
     * 转换为StandardSourceModel对象
     * @param standardSource
     * @return
     */
    public StandardSourceModel getSourceByKey(StandardSource standardSource) {
        StandardSourceModel standardSourceModel = new StandardSourceModel();
        standardSourceModel.setId(standardSource.getId());
        standardSourceModel.setCode(standardSource.getCode());
        standardSourceModel.setName(standardSource.getName());
        standardSourceModel.setType(standardSource.getSourceType());
        standardSourceModel.setTypeName(standardSource.getSourceValue());
        standardSourceModel.setCreateDate(DateUtil.toString(standardSource.getCreateDate(), DateUtil.DEFAULT_YMDHMSDATE_FORMAT));
        standardSourceModel.setCreateUser(standardSource.getCreateUser());
        standardSourceModel.setUpdateDate(DateUtil.toString(standardSource.getUpdateDate(), DateUtil.DEFAULT_YMDHMSDATE_FORMAT));
        standardSourceModel.setUpdateUser(standardSource.getUpdateUser());
        standardSourceModel.setDescription(standardSource.getDescription());
        return standardSourceModel;
    }

    /**
     * 标准来源分页搜索(名称或代码、类型)
     * @param searchCode
     * @param searchName
     * @param searchType
     * @param page
     * @param rows
     * @return
     */
    public List<StandardSource> getSourceByKey(String searchCode, String searchName, String searchType, int page, int rows) {
        Session session = currentSession();
        String strSql =  "select  src from StandardSource src WHERE 1=1 ";
        if (!StringUtils.isEmpty(searchCode)) {
            strSql += " and (code like :searchCode ";
            if(!StringUtils.isEmpty(searchName))
                strSql += " or name like :searchName)";
            else
                strSql += ") ";
        }
        else if(!StringUtils.isEmpty(searchName)){
            strSql += " and name like :searchName ";
        }
        if (!StringUtil.isEmpty(searchType)) {
            strSql += " and source_type = :searchType";
        }

        Query query = session.createQuery(strSql);
        if (!StringUtils.isEmpty(searchCode)) {
            query.setParameter("searchCode", "%" + searchCode + "%");
        }
        if (!StringUtils.isEmpty(searchName)) {
            query.setParameter("searchName", "%" + searchName + "%");
        }
        if (!StringUtil.isEmpty(searchType)) {
            query.setParameter("searchType", searchType);
        }
        query.setMaxResults(rows);
        query.setFirstResult((page - 1) * rows);
        return query.list();
    }

    /**
     * 过滤后的标准来源总数
     * @param searchCode
     * @param searchName
     * @param searchType
     * @return
     */
    public Integer getSourceByKeyInt(String searchCode, String searchName, String searchType) {
        Session session = currentSession();
        String strSql = " select count(*) FROM StandardSource where 1=1 ";
        if (!StringUtils.isEmpty(searchCode)) {
            strSql += " and (code like :searchCode ";
            if(!StringUtils.isEmpty(searchName))
                strSql += " or name like :searchName)";
            else
                strSql += ") ";
        }
        else if(!StringUtils.isEmpty(searchName)){
            strSql += " and name like :searchName ";
        }
        if (!StringUtil.isEmpty(searchType)) {
            strSql += " and source_type = :searchType";
        }

        Query query = session.createQuery(strSql);
        if (!StringUtils.isEmpty(searchCode)) {
            query.setParameter("searchCode", "%" + searchCode + "%");
        }
        if (!StringUtils.isEmpty(searchName)) {
            query.setParameter("searchName", "%" + searchName + "%");
        }
        if (!StringUtil.isEmpty(searchType)) {
            query.setString("searchType", searchType);
        }
        return ((Long)query.list().get(0)).intValue();
    }

    /**
     * 判断是否code已存在
     * @param code
     * @return
     */
    public boolean isSourceCodeExist(String code) {
        Session session = currentSession();
        String strSql = " select 1 FROM " + StandardSourceTableName;
        strSql += " where code = :code";
        Query query = session.createSQLQuery(strSql);
        query.setString("code", code);
        return query.list().size() > 0;
    }

    /**
     * 保存标准来源
     * @param standardSource
     * @return
     */
    public String saveSourceInfo(StandardSource standardSource) {
        StandardSource rs = standardSourceRepository.save(standardSource);
        return rs.getId();
    }

    /**
     * 获取当前session
     * @return
     */
    private Session currentSession(){
        return entityManager.unwrap(Session.class);
    }

}