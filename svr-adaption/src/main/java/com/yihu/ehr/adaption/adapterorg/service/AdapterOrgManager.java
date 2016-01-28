package com.yihu.ehr.adaption.adapterorg.service;

import com.yihu.ehr.adaption.commons.BaseManager;
import com.yihu.ehr.adaption.commons.FieldCondition;
import com.yihu.ehr.adaption.commons.ParmModel;
import com.yihu.ehr.adaption.feignclient.AddressClient;
import com.yihu.ehr.constrant.Result;
import com.yihu.ehr.model.address.MAddress;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/** 适配机构
 * Created by zqb on 2015/11/19.
 */
@Transactional
@Service
public class AdapterOrgManager extends BaseManager {
//    @Resource(name = Services.OrgDataSetManager)
//    XOrgDataSetManager orgDataSetManager;
//    @Resource(name = Services.OrgMetaDataManager)
//    XOrgMetaDataManager orgMetaDataManager;
//    @Resource(name = Services.OrgDictManager)
//    XOrgDictManager orgDictManager;
//    @Resource(name = Services.OrgDictItemManager)
//    XOrgDictItemManager orgDictItemManager;

    @Autowired
    AddressClient addressClient;

    @Autowired
    XAdapterOrgRepository adapterOrgRepository;

    public List<AdapterOrg> searchAdapterOrgs(ParmModel parmModel) {
        return searchAdapterOrg(parmModel);
    }

    public List<AdapterOrg> searchAdapterOrg(ParmModel parmModel) {
        Session session = currentSession();
        String hql = "select org from AdapterOrg org  ";
        String wh = parmModel.format();
        hql += wh.equals("")? "" : wh;
        Query query = setQueryVal(session.createQuery(hql), parmModel);
        int page = parmModel.getPage();
        if (page>0){
            query.setMaxResults(parmModel.getRows());
            query.setFirstResult((page - 1) * parmModel.getRows());
        }
        return query.list();
    }

    public int searchAdapterOrgInt(ParmModel parmModel) {
        Session session = currentSession();
        String hql = "select count(*) from AdapterOrg ";
        String wh = parmModel.format();
        hql += wh.equals("")? "" : wh;
        Query query = setQueryVal(session.createQuery(hql), parmModel);
        return ((Long)query.list().get(0)).intValue();
    }

    private Query setQueryVal(Query query, ParmModel parmModel){
        Map<String, FieldCondition> filters = parmModel.getFilters();
        for(String k: filters.keySet()){
            if(filters.get(k).getLogic().equals("in"))
                query.setParameterList(k, (Object[])filters.get(k).formatVal());
            else
                query.setParameter(k, filters.get(k).formatVal());
        }
        return query;
    }

    public AdapterOrg getAdapterOrg(String code) {
        return adapterOrgRepository.findOne(code);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean addAdapterOrg(AdapterOrg adapterOrg) {
        String parent;
        try{
            //地址检查并保存
            if (adapterOrg.getmAddress()!=null){
                MAddress address = adapterOrg.getmAddress();
                address.setCity("TEST");
                address.setProvince("TEST");
                address.setDistrict("TEST");
                address.setTown("TEST");
                address.setStreet("TEST");
                address.setExtra("TEST");
                address.setPostalCode("TEST");
                Result addressId = addressClient.saveAddress(address.getCountry(), address.getProvince(), address.getCity(), address.getDistrict(), address.getTown(),
                        address.getStreet(), address.getExtra(), address.getPostalCode());
//                adapterOrg.setArea((String)addressId);
            }
            adapterOrgRepository.save(adapterOrg);
            //拷贝采集标准
//            parent=adapterOrg.getParent();
//            if (parent!=null && !parent.equals("")){
//                copy(adapterOrg.getCode(), parent);
//            }
        }catch (Exception ex){
            return false;
        }
        return true;
    }


//    public boolean updateAdapterOrg(AdapterOrg adapterOrg) {
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
//    @Transactional(propagation= Propagation.REQUIRED)
//    public void deleteData(String code){
//        Query query=null;
//        //数据集
//        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
//        query = session.createQuery("delete from OrgDataSet where organization = :code");
//        query.setParameter("code", code);
//        query.executeUpdate();
//
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


//
//    public AdapterOrgModel getAdapterOrg(XAdapterOrg adapterOrg){
//        AdapterOrgModel adapterOrgModel = new  AdapterOrgModel();
//        adapterOrgModel.setCode(adapterOrg.getCode());
//        adapterOrgModel.setName(adapterOrg.getName());
//        adapterOrgModel.setDescription(adapterOrg.getDescription());
//        String parent = adapterOrg.getParent();
//        if (parent!=null&&!parent.equals("")){
//            adapterOrgModel.setParent(parent);
//            adapterOrgModel.setParentValue(getAdapterOrg(parent)!=null?getAdapterOrg(parent).getName():"");
//        }
//        XOrganization org = adapterOrg.getOrg();
//        if(org!=null){
//            adapterOrgModel.setOrg(org.getOrgCode());
//            adapterOrgModel.setOrgValue(org.getFullName());
//        }
//        AdapterType type = adapterOrg.getType();
//        if(type!=null){
//            adapterOrgModel.setType(type.getCode());
//            adapterOrgModel.setTypeValue(type.getValue());
//        }
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


//
//    public boolean isExistData(String org){
//        return orgDataSetManager.getMaxSeq(org)+orgMetaDataManager.getMaxSeq(org)+ orgDictManager.getMaxSeq(org)+orgDictItemManager.getMaxSeq(org)>0;
//    }
//
//    /**
//     * 拷贝
//     * 2015-12-31  速度优化以及添加事务控制
//     * @param code
//     * @param parent
//     * @return
//     */
//    @Transactional(propagation= Propagation.REQUIRED)
//    public boolean copy(String code,String parent){
//        Session session = currentSession();
//        String hql;
//        Query query=null;
//
//        //数据集拷贝
//        String sql = "insert into org_std_dataset(code,name,create_date,update_date,create_user,update_user,description,organization,sequence)  " +
//                "  select t.code,t.name,t.create_date,t.update_date,t.create_user,t.update_user,t.description,:code as organization,t.sequence from org_std_dataset t where t.organization=:parent ";
//        Query sqlQuery = session.createSQLQuery(sql);
//        sqlQuery.setParameter("code", code);
//        sqlQuery.setParameter("parent", parent);
//        int rows = sqlQuery.executeUpdate();
//
//        //数据元拷贝
//        sql = "insert into org_std_metadata(code,name,create_date,update_date,create_user,update_user,description,organization,sequence,org_dataset,column_type,column_length) " +
//                "  select t.code,t.name,t.create_date,t.update_date,t.create_user,t.update_user,t.description,:code as organization,t.sequence,t.org_dataset,t.column_type,t.column_length " +
//                "from org_std_metadata t where t.organization=:parent ";
//        sqlQuery = session.createSQLQuery(sql);
//        sqlQuery.setParameter("code", code);
//        sqlQuery.setParameter("parent", parent);
//        rows = sqlQuery.executeUpdate();
//
//        //字典拷贝
//        sql = "insert into org_std_dict(code,name,create_date,update_date,create_user,update_user,description,organization,sequence) " +
//                "  select t.code,t.name,t.create_date,t.update_date,t.create_user,t.update_user,t.description,:code as organization,t.sequence " +
//                "from org_std_dict t where t.organization=:parent ";
//        sqlQuery = session.createSQLQuery(sql);
//        sqlQuery.setParameter("code", code);
//        sqlQuery.setParameter("parent", parent);
//        rows = sqlQuery.executeUpdate();
//
//        //字典项拷贝
//        sql = "insert into org_std_dictentry(code,name,create_date,update_date,create_user,update_user,description,organization,sequence,sort,org_dict) " +
//                "  select t.code,t.name,t.create_date,t.update_date,t.create_user,t.update_user,t.description,:code as organization,t.sequence,t.sort,t.org_dict " +
//                "from org_std_dictentry t where t.organization=:parent ";
//        sqlQuery = session.createSQLQuery(sql);
//        sqlQuery.setParameter("code", code);
//        sqlQuery.setParameter("parent", parent);
//        rows = sqlQuery.executeUpdate();
//        return true;
//    }
}
