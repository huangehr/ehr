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
 * 机构数据集管理器。
 * @author linaz
 * @version 1.0
 * @created 23-10月-2015 10:19:06
 */
@Service(Services.OrgDataSetManager)
public class OrgDataSetManager extends SQLGeneralDAO implements XOrgDataSetManager{

    @Override
    public XOrgDataSet[] getOrgDataSetByOrgId(String orgId, int from, int count) {
        return new XOrgDataSet[0];
    }

    @Override
    @Transactional(propagation= Propagation.SUPPORTS)
    public XOrgDataSet getOrgDataSet(long id) {
        return getEntity(OrgDataSet.class, id);
    }

    @Override
    @Transactional(propagation= Propagation.SUPPORTS)
    public XOrgDataSet createOrgDataSet(XOrgDataSet orgDataSet) {
        orgDataSet.setSequence(getMaxSeq(orgDataSet.getOrganization())+1);
        saveEntity(orgDataSet);
        return orgDataSet;
    }
    public int getMaxSeq(String orgCode){
        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query query = session.createQuery("select max(sequence) from OrgDataSet where organization = :orgCode");
        query.setParameter("orgCode",orgCode);
        Integer seq = (Integer)query.uniqueResult() ;
        if (seq==null) {
            seq=0;
        }
        return seq;
    }

    @Override
    @Transactional(propagation= Propagation.SUPPORTS)
    public int updateOrgDataSet(XOrgDataSet orgDataSet) {
        saveEntity(orgDataSet);
        return 1;
    }

    @Override
    @Transactional(propagation= Propagation.SUPPORTS)
    public int deleteOrgDataSet(long id) {
        //todo:删除数据元
        Query query = currentSession().createQuery("delete from OrgDataSet where id = :id");
        query.setLong("id", id);
        query.executeUpdate();
        return 1;
    }

    @Override
    @Transactional(propagation= Propagation.SUPPORTS)
    public boolean isExistOrgDataSet(String orgCode,String code,String name) {
        Session session = currentSession();
        String hql="";
        hql = "from OrgDataSet where organization = :orgCode and code = :code ";
        Query query = session.createQuery(hql); //
        query.setString("code", code);
        query.setString("orgCode", orgCode);
        return query.list().size() != 0;
    }

    @Transactional(propagation= Propagation.SUPPORTS)
    public  List<XOrgDataSet> searchOrgDataSet(Map<String, Object> conditionMap) {
        String orgCode = (String) conditionMap.get("orgCode");
        String code = (String) conditionMap.get("code");
        Integer page = (Integer) conditionMap.get("page");
        Integer rows = (Integer) conditionMap.get("rows");

        Session session = currentSession();
        //动态SQL文拼接
        StringBuilder sb = new StringBuilder();

        sb.append("from OrgDataSet where 1=1 and organization = '"+orgCode+"'");

        if (!(code==null || code.equals(""))) {
            sb.append(" and (code like '%" + code + "%' or name like '%" + code + "%')");
        }

        sb.append(" order by  name asc");
        String hql = sb.toString();
        Query query = session.createQuery(hql);
        if (page!=null&&page>0) {
            query.setMaxResults(rows);
            query.setFirstResult((page - 1) * rows);
        }
        return query.list();
    }

    @Override
    @Transactional(propagation= Propagation.SUPPORTS)
    public List<OrgDataSetModel> searchOrgDataSets(Map<String, Object> conditionMap) {
        List<XOrgDataSet> orgDataSetList  = searchOrgDataSet(conditionMap);
        List<OrgDataSetModel> orgDictModelList = new ArrayList<>();
        for(XOrgDataSet orgDataSet : orgDataSetList){
            OrgDataSetModel orgDataSetModel = new OrgDataSetModel();
            orgDataSetModel.setId(Long.toString(orgDataSet.getId()));
            orgDataSetModel.setCode(orgDataSet.getCode());
            orgDataSetModel.setName(orgDataSet.getName());
            orgDataSetModel.setSequence(orgDataSet.getSequence());
            orgDictModelList.add(orgDataSetModel);
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

        sb.append("select 1 from OrgDataSet where 1=1 and organization = '"+orgCode+"'");
        if (!(code==null || code.equals(""))) {
            sb.append(" and (code like '%" + code + "%' or name like '%" + code + "%')");
        }
        String hql = sb.toString();
        Query query = session.createQuery(hql);

        return query.list().size();
    }
}
