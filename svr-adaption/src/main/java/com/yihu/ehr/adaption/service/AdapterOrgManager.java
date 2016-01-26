//package com.yihu.ehr.adaption.service;
//
//import com.yihu.ha.constrant.Services;
//import com.yihu.ha.data.sql.SQLGeneralDAO;
//import com.yihu.ha.dict.model.common.AdapterType;
//import com.yihu.ha.geography.model.Address;
//import com.yihu.ha.geography.model.XAddressManager;
//import org.hibernate.Query;
//import org.hibernate.Session;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.Resource;
//import javax.transaction.Transactional;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
///** 适配机构
// * Created by zqb on 2015/11/19.
// */
//@Transactional
//@Service(Services.AdapterOrgManager)
//public class AdapterOrgManager extends SQLGeneralDAO implements XAdapterOrgManager {
//    @Resource(name = Services.OrgDataSetManager)
//    XOrgDataSetManager orgDataSetManager;
//    @Resource(name = Services.OrgMetaDataManager)
//    XOrgMetaDataManager orgMetaDataManager;
//    @Resource(name = Services.OrgDictManager)
//    XOrgDictManager orgDictManager;
//    @Resource(name = Services.OrgDictItemManager)
//    XOrgDictItemManager orgDictItemManager;
//    @Resource(name = Services.AddressManager)
//    XAddressManager addressManager;
//
//    @Override
//    public boolean addAdapterOrg(XAdapterOrg adapterOrg) {
//        String parent;
//        try{
//            //地址检查并保存
//            if (adapterOrg.getArea()!=null){
//                adapterOrg.setArea(addressManager.saveAddress(adapterOrg.getArea()));
//            }
//            saveEntity(adapterOrg);
//            //拷贝采集标准
//            parent=adapterOrg.getParent();
//            if (parent!=null && !parent.equals("")){
//                copy(adapterOrg.getCode(), parent);
//            }
//        }catch (Exception ex){
//            return false;
//        }
//        return true;
//    }
//
//    @Override
//    public boolean updateAdapterOrg(XAdapterOrg adapterOrg) {
//        try {
//            //地址检查并保存
//            if (adapterOrg.getArea()!=null){
//                adapterOrg.setArea(addressManager.saveAddress(adapterOrg.getArea()));
//            }
//            updateEntity(adapterOrg);
//        }catch (Exception e){
//            return false;
//        }
//        return true;
//    }
//
//    @Override
//    public boolean deleteAdapterOrg(XAdapterOrg adapterOrg) {
//        try {
//            deleteEntity(adapterOrg);
//        }catch (Exception e){
//            return false;
//        }
//        return true;
//    }
//
//    @Override
//    public int deleteAdapterOrg(List<String> codes) {
//        //删除对应的数据集、字典
//        for(String code:codes){
//            deleteData(code);
//        }
//        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
//        Query query = session.createQuery("delete from AdapterOrg where code in (:codes)");
//        query.setParameterList("codes", codes);
//        return query.executeUpdate();
//    }
//
//    //删除机构下的采集标准数据
//    @Override
//    public void deleteData(String code){
//        Query query=null;
//        //数据集
//        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
//        query = session.createQuery("delete from OrgDataSet where organization = :code");
//        query.setParameter("code", code);
//        query.executeUpdate();
//        //数据元
//        query = session.createQuery("delete from OrgMetaData where organization = :code");
//        query.setParameter("code", code);
//        query.executeUpdate();
//        //字典
//        query = session.createQuery("delete from OrgDict where organization = :code");
//        query.setParameter("code", code);
//        query.executeUpdate();
//        //字典项
//        query = session.createQuery("delete from OrgDictItem where organization = :code");
//        query.setParameter("code", code);
//        query.executeUpdate();
//    }
//
//    @Override
//    public XAdapterOrg getAdapterOrg(String code) {
//        XAdapterOrg adapterOrg = getEntity(AdapterOrg.class,code);
//        return adapterOrg;
//    }
//
//    public AdapterOrgModel getAdapterOrg(XAdapterOrg adapterOrg){
//        AdapterOrgModel adapterOrgModel = new  AdapterOrgModel();
//        adapterOrgModel.setCode(adapterOrg.getCode());
//        adapterOrgModel.setName(adapterOrg.getName());
//        adapterOrgModel.setDescription(adapterOrg.getDescription());
//        String parent = adapterOrg.getParent();
//        if (parent!=null&&!parent.equals("")){
//            adapterOrgModel.setParent(parent);
//            adapterOrgModel.setParentValue(getAdapterOrg(parent).getName());
//        }
//        adapterOrgModel.setOrg(adapterOrg.getOrg().getOrgCode());
//        adapterOrgModel.setOrgValue(adapterOrg.getOrg().getFullName());
//        adapterOrgModel.setArea((Address)adapterOrg.getArea());
//        adapterOrgModel.setType(adapterOrg.getType().getCode());
//        adapterOrgModel.setTypeValue(adapterOrg.getType().getValue());
//        if (adapterOrg.getArea()!=null){
//            adapterOrgModel.setArea((Address)adapterOrg.getArea());
//        }
//        return  adapterOrgModel;
//    }
//
//    @Override
//    public List<XAdapterOrg> searchAdapterOrg(Map<String, Object> args) {
//        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
//        String key = (String)args.get("key");
//        Integer page = (Integer) args.get("page");
//        Integer pageSize = (Integer) args.get("pageSize");
//        List<AdapterType> typeLs = (List) args.get("typeLs");
////        AdapterType type = (AdapterType) args.get("type");
//        String hql = "from AdapterOrg where (code like :key or name like :key)";
//        if(typeLs!=null){
//            for(int i=0;i<typeLs.size();i++){
//                if(i==0){
//                    if(typeLs.size()==1)
//                        hql += " and type =:type"+i+" ";
//                    else
//                        hql += " and (type =:type"+i+" ";
//                }
//                else if(i==typeLs.size()-1)
//                    hql += " or type =:type"+i+") ";
//                else
//                    hql += "or type =:type"+i+" ";
//            }
//        }
//
//        Query query = session.createQuery(hql);
//        query.setString("key", "%"+key+"%");
//        if(typeLs!=null){
//            for(int i=0;i<typeLs.size();i++){
//                query.setParameter("type"+i, typeLs.get(i));
//            }
//        }
//
//        if (page!=null && page>0){
//            query.setMaxResults(pageSize);
//            query.setFirstResult((page - 1) * pageSize);
//        }
//        List<XAdapterOrg> adapterOrgs = query.list();
//        return adapterOrgs;
//    }
//
//    @Override
//    public List<AdapterOrgModel> searchAdapterOrgs(Map<String, Object> args) {
//        List<XAdapterOrg> adapterOrgList = searchAdapterOrg(args);
//        List<AdapterOrgModel> adapterOrgModels = new ArrayList<>();
//        for(XAdapterOrg adapterOrg:adapterOrgList){
//            AdapterOrgModel adapterOrgModel = getAdapterOrg(adapterOrg);
//            adapterOrgModels.add(adapterOrgModel);
//        }
//        return adapterOrgModels;
//    }
//
//    @Override
//    public int searchAdapterOrgInt(Map<String, Object> args) {
//        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
//        String key = (String)args.get("key");
//        List<AdapterType> typeLs = (List) args.get("typeLs");
//        String hql = "select 1 from AdapterOrg where (code like :key or name like :key)";
//        if(typeLs!=null){
//            for(int i=0;i<typeLs.size();i++){
//                if(i==0){
//                    if(typeLs.size()==1)
//                        hql += " and type =:type"+i+" ";
//                    else
//                        hql += " and (type =:type"+i+" ";
//                }
//                else if(i==typeLs.size()-1)
//                    hql += " or type =:type"+i+") ";
//                else
//                    hql += "or type =:type"+i+" ";
//            }
//        }
//        Query query = session.createQuery(hql);
//        if(typeLs!=null){
//            for(int i=0;i<typeLs.size();i++){
//                query.setParameter("type"+i, typeLs.get(i));
//            }
//        }
//        query.setString("key", "%"+key+"%");
//
//        return query.list().size();
//    }
//
//    @Override
//    public List<XAdapterOrg> searchAdapterOrg(AdapterType type) {
//        Session session = currentSession();
//        String hql = " from AdapterOrg where 1=1 ";
//        if (type!=null){
//            hql+=" and type=:type ";
//        }
//        Query query = session.createQuery(hql);
//        if (type!=null) {
//            query.setParameter("type", type);
//        }
//        return query.list();
//    }
//
//    public boolean isExistData(String org){
//        return orgDataSetManager.getMaxSeq(org)+orgMetaDataManager.getMaxSeq(org)+ orgDictManager.getMaxSeq(org)+orgDictItemManager.getMaxSeq(org)>0;
//    }
//
//    //拷贝
//    public boolean copy(String code,String parent){
//        try{
//            Session session = currentSession();
//            String hql;
//            Query query=null;
//            //数据集拷贝
//            hql=" from OrgDataSet where organization = '"+parent+"'";
//            query = session.createQuery(hql);
//            List<XOrgDataSet> orgDataSetList = query.list();
//            for(XOrgDataSet orgDataSet:orgDataSetList){
//                XOrgDataSet newOrgDataSet = new OrgDataSet();
//                newOrgDataSet.setNewObject(orgDataSet);
//                newOrgDataSet.setOrganization(code);
//                saveEntity(newOrgDataSet);
//            }
//            //数据元拷贝
//            hql=" from OrgMetaData  where organization = '"+parent+"'";
//            query = session.createQuery(hql);
//            List<XOrgMetaData> orgMetaDataList = query.list();
//            for(XOrgMetaData orgMetaData:orgMetaDataList){
//                XOrgMetaData newOrgMetaData = new OrgMetaData();
//                newOrgMetaData.setNewObject(orgMetaData);
//                newOrgMetaData.setOrganization(code);
//                saveEntity(newOrgMetaData);
//            }
//            //字典拷贝
//            hql=" from OrgDict where organization = '"+parent+"'";
//            query = session.createQuery(hql);
//            List<XOrgDict> orgDictList = query.list();
//            for(XOrgDict orgDict:orgDictList){
//                XOrgDict newOrgDict = new OrgDict();
//                newOrgDict.setNewObject(orgDict);
//                newOrgDict.setOrganization(code);
//                saveEntity(newOrgDict);
//            }
//            //字典项拷贝
//            hql=" from OrgDictItem  where organization = '"+parent+"'";
//            query = session.createQuery(hql);
//            List<XOrgDictItem> orgDictItemList = query.list();
//            for(XOrgDictItem orgDictItem:orgDictItemList){
//                XOrgDictItem newOrgDictItem = new OrgDictItem();
//                newOrgDictItem.setNewObject(orgDictItem);
//                newOrgDictItem.setOrganization(code);
//                saveEntity(newOrgDictItem);
//            }
//        }catch (Exception ex){
//            return  false;
//        }
//        return true;
//    }
//}
