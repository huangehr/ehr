//package com.yihu.ehr.adaption.service;
//
//import com.yihu.ha.constrant.Services;
//import com.yihu.ha.data.sql.SQLGeneralDAO;
//import com.yihu.ha.organization.model.XOrganization;
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
// * 机构字典明细管理器。
// * @author linaz
// * @version 1.0
// * @created 23-10月-2015 10:19:06
// */
//
//@Service(Services.OrgDictItemManager)
//public class OrgDictItemManager extends SQLGeneralDAO implements  XOrgDictItemManager{
//
//
//    @Override
//    public XOrgDictItem[] getOrgDictItemByDictId(long dictId) {
//        return new XOrgDictItem[0];
//    }
//
//    @Override
//    @Transactional(propagation= Propagation.SUPPORTS)
//    public XOrgDictItem getOrgDictItem(long id) {
//        return (XOrgDictItem) getEntity(OrgDictItem.class, id);
//    }
//
//    @Override
//    @Transactional(propagation= Propagation.SUPPORTS)
//    public XOrgDictItem createOrgDictItem(XOrgDictItem orgDictItem) {
//        orgDictItem.setSequence(getMaxSeq(orgDictItem.getOrganization())+1);
//        saveEntity(orgDictItem);
//        return orgDictItem;
//    }
//    public int getMaxSeq(String orgCode){
//        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
//        Query query = session.createQuery("select max(sequence) from OrgDictItem where organization = :orgCode");
//        query.setParameter("orgCode",orgCode);
//        Integer seq = (Integer)query.uniqueResult() ;
//        if (seq==null) {
//            seq=0;
//        }
//        return seq;
//    }
//
//    @Override
//    @Transactional(propagation= Propagation.SUPPORTS)
//    public int updateOrgDictItem(XOrgDictItem orgDictItem) {
//        saveEntity(orgDictItem);
//        return 1;
//    }
//
//    @Override
//    @Transactional(propagation= Propagation.SUPPORTS)
//    public int deleteOrgDictItem(long id) {
//        Query query = currentSession().createQuery("delete from OrgDictItem where id = :id");
//        query.setLong("id", id);
//        query.executeUpdate();
//        return 1;
//    }
//
//    @Override
//    @Transactional(propagation= Propagation.SUPPORTS)
//    public int deleteOrgDictItemList(Long[] ids) {
//        List<Long> idLst= new ArrayList<>();
//        idLst =  Arrays.asList(ids);
//        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
//        Query query = session.createQuery("delete from OrgDictItem where id in (:ids)");
//        query.setParameterList("ids", idLst);
//        return query.executeUpdate();
//    }
//
//    @Override
//    @Transactional(propagation= Propagation.SUPPORTS)
//    public boolean isExistOrgDictItem(Integer orgDictSeq,String orgCode,String code,String name) {
//        Session session = currentSession();
//        Query query = session.createQuery("from OrgDictItem where orgDict =:orgDictSeq and organization = :orgCode and code = :code ");
//        query.setString("code", code);
//        query.setParameter("orgCode",orgCode);
//        query.setParameter("orgDictSeq", orgDictSeq);
//        return query.list().size() != 0;
//    }
//
//    @Transactional(propagation= Propagation.SUPPORTS)
//    public List<XOrgDictItem> searchOrgDictItem(Map<String, Object> conditionMap) {
//        Integer orgDictSeq = (Integer) conditionMap.get("orgDictSeq");
//        String orgCode = (String) conditionMap.get("orgCode");
//        String code = (String) conditionMap.get("code");
//        Integer page = (Integer) conditionMap.get("page");
//        Integer rows = (Integer) conditionMap.get("rows");
//
//        Session session = currentSession();
//        //动态SQL文拼接
//        StringBuilder sb = new StringBuilder();
//        sb.append("from OrgDictItem where 1=1 and orgDict = '"+orgDictSeq+"' and organization = '"+orgCode+"'");
//        if (!(code==null || code.equals(""))) {
//            sb.append(" and (code like '%" + code + "%' or name like '%" + code + "%')");
//        }
//        sb.append(" order by  name asc");
//        String hql = sb.toString();
//        Query query = session.createQuery(hql);
//        if (page!=null&&page>0){
//            query.setMaxResults(rows);
//            query.setFirstResult((page - 1) * rows);
//        }
//        return query.list();
//    }
//
//    @Override
//    @Transactional(propagation= Propagation.SUPPORTS)
//    public List<OrgDictItemModel> searchOrgDictItems(Map<String, Object> conditionMap) {
//        List<XOrgDictItem> orgDictItemList  = searchOrgDictItem(conditionMap);
//        List<OrgDictItemModel> orgDictItemModelList = new ArrayList<>();
//        for(XOrgDictItem orgDictItem : orgDictItemList){
//            OrgDictItemModel orgDictItemModel = new OrgDictItemModel();
//            orgDictItemModel.setId(Long.toString(orgDictItem.getId()));
//            orgDictItemModel.setCode(orgDictItem.getCode());
//            orgDictItemModel.setName(orgDictItem.getName());
//            orgDictItemModel.setSort(Integer.toString(orgDictItem.getSort()));
//            orgDictItemModelList.add(orgDictItemModel);
//        }
//        return orgDictItemModelList;
//    }
//
//    @Override
//    @Transactional(propagation= Propagation.SUPPORTS)
//    public int searchTotalCount(Map<String, Object> conditionMap) {
//
//        String code = (String) conditionMap.get("code");
//        Integer orgDictSeq = (Integer) conditionMap.get("orgDictSeq");
//        String orgCode = (String) conditionMap.get("orgCode");
//
//        Session session = currentSession();
//        //动态SQL文拼接
//        StringBuilder sb = new StringBuilder();
//
//        sb.append("select 1 from OrgDictItem where 1=1 and orgDict ='"+orgDictSeq+"' and organization = '"+orgCode+"'");
//        if (!(code==null || code.equals(""))) {
//            sb.append(" and (code like '%" + code + "%' or name like '%" + code + "%')");
//        }
//        String hql = sb.toString();
//        Query query = session.createQuery(hql);
//
//        return query.list().size();
//    }
//
//    @Override
//    @Transactional(propagation= Propagation.SUPPORTS)
//    public int getNextSort(long dictId) {
//        Session session = currentSession();
//        Query query = session.createQuery("select max(sort) from OrgDictItem  where orgDict= '"+dictId+"'");
//        int result;
//        if(query.uniqueResult()==null){
//            result = 1;
//        }else{
//            result = Integer.parseInt(query.uniqueResult().toString())+1;
//        }
//        return result;
//    }
//
//    @Override
//    @Transactional(propagation= Propagation.SUPPORTS)
//    public List<XOrganization> getOrganizationList(){
//        Session session = currentSession();
//        Query query = session.createQuery("from Organization order by fullName desc");
//        return query.list();
//    }
//
//    @Transactional(propagation= Propagation.SUPPORTS)
//    public List<XOrgDictItem> getAllOrgDictItem(Map<String, Object> conditionMap) {
//        String orgCode = (String) conditionMap.get("orgCode");
//        String code = (String) conditionMap.get("code");
//        Integer page = (Integer) conditionMap.get("page");
//        Integer rows = (Integer) conditionMap.get("rows");
//
//        Session session = currentSession();
//        //动态SQL文拼接
//        StringBuilder sb = new StringBuilder();
//        sb.append("from OrgDictItem where 1=1 and organization = '"+orgCode+"'");
//        if (!(code==null || code.equals(""))) {
//            sb.append(" and (code like '%" + code + "%' or name like '%" + code + "%')");
//        }
//        sb.append(" order by  name asc");
//        String hql = sb.toString();
//        Query query = session.createQuery(hql);
//        if (page!=null&&page>0){
//            query.setMaxResults(rows);
//            query.setFirstResult((page - 1) * rows);
//        }
//        return query.list();
//    }
//}
