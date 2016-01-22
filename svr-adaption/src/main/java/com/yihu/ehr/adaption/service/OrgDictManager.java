package com.yihu.ehr.adaption.service;

import com.yihu.ha.constrant.Services;
import com.yihu.ha.data.sql.SQLGeneralDAO;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 机构字典管理器。
 * @author linaz
 * @version 1.0
 * @created 23-10月-2015 10:19:06
 */
@Service(Services.OrgDictManager)
public class OrgDictManager extends SQLGeneralDAO implements XOrgDictManager{

    @Override
    public XOrgDict[] getDictByOrgId(String orgId, int from, int count) {
        return new XOrgDict[0];
    }

    @Override
    @Transactional(propagation= Propagation.SUPPORTS)
    public XOrgDict getOrgDict(long id) {
        return getEntity(OrgDict.class, id);
    }

    @Override
    @Transactional(propagation= Propagation.SUPPORTS)
    public XOrgDict createOrgDict(XOrgDict orgDict) {
        orgDict.setSequence(getMaxSeq(orgDict.getOrganization())+1);
        saveEntity(orgDict);
        return orgDict;
    }

    public int getMaxSeq(String orgCode){
        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query query = session.createQuery("select max(sequence) from OrgDict where organization = :orgCode");
        query.setParameter("orgCode",orgCode);
        Integer seq = (Integer)query.uniqueResult() ;
        if (seq==null) {
            seq=0;
        }
        return seq;
    }

    @Override
    @Transactional(propagation= Propagation.SUPPORTS)
    public int updateOrgDict(XOrgDict orgDict) {
        saveEntity(orgDict);
        return 1;
    }

    @Override
    @Transactional(propagation= Propagation.SUPPORTS)
    public int deleteOrgDict(long id) {
        Query query = currentSession().createQuery("delete from OrgDict where id = :id");
        query.setLong("id", id);
        query.executeUpdate();
        query = currentSession().createQuery("delete from OrgDictItem where orgDict.id = :id");
        query.setLong("id", id);
        query.executeUpdate();
        return 1;
    }

    @Override
    @Transactional(propagation= Propagation.SUPPORTS)
    public boolean isExistOrgDict(String orgCode,String code,String name) {
        Session session = currentSession();
        String hql="";
        hql = "from OrgDict where organization = :orgCode and (code = :code )";
        Query query = session.createQuery(hql); //
        query.setString("code", code);
        query.setString("orgCode", orgCode);
        return query.list().size() != 0;
    }

    @Transactional(propagation= Propagation.SUPPORTS)
    public List<XOrgDict> searchOrgDict(Map<String, Object> conditionMap){
        String orgCode = (String) conditionMap.get("orgCode");
        String code = (String) conditionMap.get("code");
        Integer page = (Integer) conditionMap.get("page");
        Integer rows = (Integer) conditionMap.get("rows");

        Session session = currentSession();
        //动态SQL文拼接
        StringBuilder sb = new StringBuilder();
        sb.append("from OrgDict");
        sb.append(" where 1=1  and organization ='"+orgCode+"'");

        if (!(code==null || code.equals(""))) {
            sb.append(" and (code like '%" + code + "%' or name like '%" + code + "%')");
        }
        sb.append(" order by name asc");
        String hql = sb.toString();
        Query query = session.createQuery(hql);
        if (page!=null&&page>0){
            query.setMaxResults(rows);
            query.setFirstResult((page - 1) * rows);
        }
        return query.list();
    }

    @Override
    @Transactional(propagation= Propagation.SUPPORTS)
    public List<OrgDictModel> searchOrgDicts(Map<String, Object> conditionMap) {
        List<XOrgDict> orgDictList = searchOrgDict(conditionMap);
        List<OrgDictModel> orgDictModelList = new ArrayList<>();
        for(XOrgDict orgDict : orgDictList){
            OrgDictModel orgDictModel = new OrgDictModel();
            orgDictModel.setId(Long.toString(orgDict.getId()));
            orgDictModel.setCode(orgDict.getCode());
            orgDictModel.setName(orgDict.getName());
            orgDictModel.setSequence(orgDict.getSequence());
            orgDictModelList.add(orgDictModel);
        }
        return orgDictModelList;
    }



    @Override
    @Transactional(propagation= Propagation.SUPPORTS)
    public int searchTotalCount(Map<String, Object> conditionMap) {

        String code = (String) conditionMap.get("code");
        String orgCode = (String) conditionMap.get("orgCode");
        Session session = currentSession();
        //动态SQL文拼接
        StringBuilder sb = new StringBuilder();
        sb.append("from OrgDict");
        sb.append(" where 1=1 and organization ='"+orgCode+"'");

        if (!(code==null || code.equals(""))) {
            sb.append(" and (code like '%" + code + "%' or name like '%" + code + "%')");
        }
        String hql = sb.toString();
        Query query = session.createQuery(hql);
        return query.list().size();
    }
}
