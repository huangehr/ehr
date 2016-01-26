//package com.yihu.ehr.adaption.service;
//
//import com.yihu.ha.constrant.Services;
//import com.yihu.ha.data.sql.SQLGeneralDAO;
//import org.hibernate.Query;
//import org.hibernate.Session;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Propagation;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//
///**
// * 机构数据元实现
// * @author linaz
// * @version 1.0
// * @created 23-10月-2015 10:19:06
// */
//@Service(Services.OrgMetaDataManager)
//public class OrgMetaDataManager extends SQLGeneralDAO implements XOrgMetaDataManager {
//
//
//    @Override
//    public XOrgMetaData[] getOrgMetaDataBySetId(String setId, int from, int count) {
//        return new XOrgMetaData[0];
//    }
//
//    @Override
//    @Transactional(propagation = Propagation.SUPPORTS)
//    public XOrgMetaData getOrgMetaData(long id) {
//        return (XOrgMetaData) getEntity(OrgMetaData.class, id);
//    }
//
//    @Override
//    @Transactional(propagation = Propagation.SUPPORTS)
//    public XOrgMetaData createOrgMetaData(XOrgMetaData orgMetaData) {
//        orgMetaData.setSequence(getMaxSeq(orgMetaData.getOrganization())+1);
//        saveEntity(orgMetaData);
//        return orgMetaData;
//    }
//
//    public int getMaxSeq(String orgCode){
//        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
//        Query query = session.createQuery("select max(sequence) from OrgMetaData where organization = :orgCode");
//        query.setParameter("orgCode",orgCode);
//        Integer seq = (Integer)query.uniqueResult() ;
//        if (seq==null) {
//            seq=0;
//        }
//        return seq;
//    }
//
//    @Override
//    @Transactional(propagation = Propagation.SUPPORTS)
//    public int updateOrgMetaData(XOrgMetaData orgMetaData) {
//        updateEntity(orgMetaData);
//        return 1;
//    }
//
//    @Override
//    @Transactional(propagation = Propagation.SUPPORTS)
//    public int deleteOrgMetaData(long id) {
//        Query query = currentSession().createQuery("delete from OrgMetaData where id = :id");
//        query.setLong("id", id);
//        query.executeUpdate();
//        return 1;
//    }
//
//    @Override
//    @Transactional(propagation = Propagation.SUPPORTS)
//    public int deleteOrgMetaDataList(Long[] ids) {
//        List<Long> idLst = new ArrayList<>();
//        idLst = Arrays.asList(ids);
//        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
//        Query query = session.createQuery("delete from OrgMetaData where id in (:ids)");
//        query.setParameterList("ids", idLst);
//        return query.executeUpdate();
//    }
//
//    @Transactional(propagation = Propagation.SUPPORTS)
//    public List<XOrgMetaData> searchOrgMetaData(Map<String, Object> conditionMap) {
//        String code = (String) conditionMap.get("code");
//        String orgCode = (String) conditionMap.get("orgCode");
//        Integer orgDataSetSeq = (Integer) conditionMap.get("orgDataSetSeq");
//        Integer page = (Integer) conditionMap.get("page");
//        Integer rows = (Integer) conditionMap.get("rows");
//        Session session = currentSession();
//        //动态SQL文拼接
//        StringBuilder sb = new StringBuilder();
//        sb.append("from OrgMetaData where 1=1 and orgDataSet = '" + orgDataSetSeq + "' and organization ='" + orgCode + "' " );
//        if (!(code == null || code.equals(""))) {
//            sb.append(" and (code like '%" + code + "%' or name like '%" + code + "%')");
//        }
//        sb.append(" order by  name asc");
//        String hql = sb.toString();
//        Query query = session.createQuery(hql);
//        if (page != null && page > 0) {
//            query.setMaxResults(rows);
//            query.setFirstResult((page - 1) * rows);
//        }
//        return query.list();
//    }
//
//    @Override
//    @Transactional(propagation = Propagation.SUPPORTS)
//    public List<OrgMetaDataModel> searchOrgMetaDatas(Map<String, Object> conditionMap) {
//        List<XOrgMetaData> orgMetaDataList = searchOrgMetaData(conditionMap);
//        List<OrgMetaDataModel> orgMetaDataModelList = new ArrayList<>();
//        for (XOrgMetaData orgMetaData : orgMetaDataList) {
//            OrgMetaDataModel orgMetaDataModel = new OrgMetaDataModel();
//            orgMetaDataModel.setId(Long.toString(orgMetaData.getId()));
//            orgMetaDataModel.setCode(orgMetaData.getCode());
//            orgMetaDataModel.setName(orgMetaData.getName());
//            orgMetaDataModelList.add(orgMetaDataModel);
//        }
//        return orgMetaDataModelList;
//    }
//
//    @Override
//    @Transactional(propagation = Propagation.SUPPORTS)
//    public boolean isExistOrgMetaData(Integer orgDataSetSeq,String orgCode, String code, String name) {
//
//        Session session = currentSession();
//        String hql;
//        Query query = null;
//        hql = "from OrgMetaData where orgDataSet= :orgDataSetSeq and organization=:orgCode  and code = :code";
//        query = session.createQuery(hql);
//
//        query.setString("code", code);
//        query.setString("orgCode", orgCode);
//        query.setParameter("orgDataSetSeq", orgDataSetSeq);
//        return query.list().size() != 0;
//
//    }
//
//    @Override
//    @Transactional(propagation = Propagation.SUPPORTS)
//    public int searchTotalCount(Map<String, Object> conditionMap) {
//        String code = (String) conditionMap.get("code");
//        String orgCode = (String) conditionMap.get("orgCode");
//        Integer orgDataSetSeq = (Integer) conditionMap.get("orgDataSetSeq");
//        Session session = currentSession();
//        //动态SQL文拼接
//        StringBuilder sb = new StringBuilder();
//        sb.append("select 1 from OrgMetaData where 1=1 and orgDataSet = '" + orgDataSetSeq + "' and organization ='" + orgCode + "' " );
//        if (!(code == null || code.equals(""))) {
//            sb.append(" and (code like '%" + code + "%' or name like '%" + code + "%')");
//        }
//        String hql = sb.toString();
//        Query query = session.createQuery(hql);
//        return query.list().size();
//    }
//
//    @Transactional(propagation = Propagation.SUPPORTS)
//    public List<XOrgMetaData> getAllOrgMetaData(Map<String, Object> conditionMap) {
//        String code = (String) conditionMap.get("code");
//        String orgCode = (String) conditionMap.get("orgCode");
//        Integer page = (Integer) conditionMap.get("page");
//        Integer rows = (Integer) conditionMap.get("rows");
//        Session session = currentSession();
//        //动态SQL文拼接
//        StringBuilder sb = new StringBuilder();
//        sb.append("from OrgMetaData where 1=1 and organization ='" + orgCode + "' " );
//        if (!(code == null || code.equals(""))) {
//            sb.append(" and (code like '%" + code + "%' or name like '%" + code + "%')");
//        }
//        sb.append(" order by  name asc");
//        String hql = sb.toString();
//        Query query = session.createQuery(hql);
//        if (page != null && page > 0) {
//            query.setMaxResults(rows);
//            query.setFirstResult((page - 1) * rows);
//        }
//        return query.list();
//    }
//}
