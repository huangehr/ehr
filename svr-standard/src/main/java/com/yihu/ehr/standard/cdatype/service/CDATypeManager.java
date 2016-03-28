package com.yihu.ehr.standard.cdatype.service;

import com.yihu.ehr.query.BaseJpaService;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author AndyCai
 * @version 1.0
 * @created 11-12月-2015 15:53:02
 */
@Transactional
@Service
public class CDATypeManager extends BaseJpaService<CDAType, XcdaTypeRepository>{


    public XcdaTypeRepository getXcdaTypeRepository(){
       return (XcdaTypeRepository) getRepository();
   }

    /**
     * 根据查询条件获取cda类别
     * @param code
     * @param name
     * @return
     */
    public List<CDAType> GetCdaTypeByCodeOrName(String code,String name) {

        Session session = currentSession();
        String strSql = "from CDAType a where 1=1";
        if (!StringUtils.isEmpty(code)) {
            strSql += " and a.code like :code";
        }
        if (!StringUtils.isEmpty(name)) {
            strSql += " and a.name like :name";
        }
        Query query = session.createQuery(strSql);
        if (!StringUtils.isEmpty(code)) {
            query.setString("code", "%" + code + "%");
        }
        if (!StringUtils.isEmpty(name)) {
            query.setString("name", "%" + name + "%");
        }
        List<CDAType> listInfo = query.list();

        return listInfo;
    }


    /**
     * @param info
     */
    public CDAType save(CDAType info) {
        return getXcdaTypeRepository().save(info);
    }

    /**
     * 根据ID删除类别
     *
     * @param ids (批量删除时id以逗号隔开)
     * @return boolResult 操作结果
     */
    public boolean deleteCdaType(String ids) {
        String[] strIds = ids.split(",");
        Session session = currentSession();
        String strSql = "delete CDAType a where a.id in(:strIds)";
        Query query = session.createQuery(strSql);
        query.setParameterList("strIds",strIds);
        query.executeUpdate();
        return true;
    }


    /**
     * 根据id 获取类别信息
     * @param id
     */
    public CDAType getCdaTypeById(String id) {
        return getXcdaTypeRepository().findOne(id);
    }


    /**
     * 根据ids获取类别信息
     * @param ids
     */
    public List<CDAType> getCDATypeByIds(String[] ids) {
        return getXcdaTypeRepository().findCDATypeByIds(ids);
    }
    /**
     * 判断代码是否重复
     *
     * @param code
     */
    public boolean isCodeExist(String code) {
        List<CDAType> listInfo = null;
        Session session = currentSession();

        String strSql = "from CDAType a where a.code = :code";

        Query query = session.createQuery(strSql);
        query.setParameter("code", code);

        return query.list().size()>0;
    }



    /**
     * 根据父级ID获取下级类别
     */
    public List<CDAType> getChildrenCDATypeByParentId(String parentId) {
        Session session = currentSession();
        String strSql="";
        if(StringUtils.isEmpty(parentId)){
            strSql += "from CDAType a where 1=1 and (a.parentId is null or a.parentId='')";
        }else{
            strSql += "from CDAType a where 1=1 and a.parentId =:parentId";
        }
        Query query = session.createQuery(strSql);
        if(!StringUtils.isEmpty(parentId)){
            query.setString("parentId", parentId);
        }
        return query.list();
    }



    public List<CDAType> getChildrenType(String strId, String strKey) {
        List<CDAType> listType = null;
        Session session = currentSession();
        String strSql = "from CDAType a where 1=1 ";
        if (strId != null && !strId.equals("")) {
            strId="'"+strId.replaceAll(",","','")+"'";
            strSql += " and a.id  in ("+strId+")";
        }
        if (strKey != null && !strKey.equals("")) {
            strSql += " and (a.code like :strkey or a.name like :strkey)";
        }
        Query query = session.createQuery(strSql);
        if (strKey != null && !strKey.equals("")) {
            query.setString("strkey", "%" + strKey + "%");
        }
        listType = query.list();
        return listType;
    }


//    public List<CDAType> getParentType(String strId, String strKey) {
//        List<CDAType> listType = null;
//        Session session = currentSession();
//        String strSql = "from CDAType a where 1=1 ";
//        if (strId != null && !strId.equals("")) {
//            strId="'"+strId.replaceAll(",","','")+"'";
//            strSql += " and a.id not in ("+strId+")";
//        }
//        if (strKey != null && !strKey.equals("")) {
//            strSql += " and (a.code like :strkey or a.name like :strkey)";
//        }
//        Query query = session.createQuery(strSql);
//        if (strKey != null && !strKey.equals("")) {
//            query.setString("strkey", "%" + strKey + "%");
//        }
//        listType = query.list();
//        return listType;
//    }

    public List<CDAType> getOtherCDAType(String id) {
        List<CDAType> list = getXcdaTypeRepository().getOtherCDAType(id);
        return list;
    }

    public List<CDAType> getCdaTypeExcludeSelfAndChildren(String childrenIds) {
        return null;
    }
}