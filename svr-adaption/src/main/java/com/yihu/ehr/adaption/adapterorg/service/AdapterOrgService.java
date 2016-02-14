package com.yihu.ehr.adaption.adapterorg.service;

import com.yihu.ehr.adaption.feignclient.AddressClient;
import com.yihu.ehr.adaption.orgdataset.service.OrgDataSetService;
import com.yihu.ehr.adaption.orgdict.service.OrgDictService;
import com.yihu.ehr.adaption.orgdictitem.service.OrgDictItemService;
import com.yihu.ehr.adaption.orgmetaset.service.OrgMetaDataService;
import com.yihu.ehr.model.address.MAddress;
import com.yihu.ehr.util.query.BaseService;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.3
 */
@Transactional
@Service
public class AdapterOrgService extends BaseService<AdapterOrg, XAdapterOrgRepository> {

    @Autowired
    AddressClient addressClient;
    @Autowired
    OrgDataSetService orgDataSetManager;
    @Autowired
    OrgMetaDataService orgMetaDataManager;
    @Autowired
    OrgDictItemService orgDictItemManager;
    @Autowired
    OrgDictService orgDictManager;

    @Transactional(propagation = Propagation.REQUIRED)
    public void addAdapterOrg(AdapterOrg adapterOrg, String apiVersion) {
        //地址检查并保存
        MAddress address = adapterOrg.getMAddress();
        if (address != null) {
            Object addressId =
                    addressClient.saveAddress(
                            apiVersion, address.getCountry(), address.getProvince(), address.getCity(), address.getDistrict(), address.getTown(),
                            address.getStreet(), address.getExtra(), address.getPostalCode());
            adapterOrg.setArea((String) addressId);
        }
        save(adapterOrg);
        //拷贝采集标准
        String parent = adapterOrg.getParent();
        if (parent != null && !parent.equals("")) {
            copy(adapterOrg.getCode(), parent);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteAdapterOrg(String[] codes) {

        for (String code : codes) {
            delete(code);
        }
    }

    /**
     * 删除机构下的采集标准数据
     *
     * @param code
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteData(String code) {
        Query query = null;
        //数据集
        Session session = currentSession();
        query = session.createQuery("delete from OrgDataSet where organization = :code");
        query.setParameter("code", code);
        query.executeUpdate();
        //数据元
        query = session.createQuery("delete from OrgMetaData where organization = :code");
        query.setParameter("code", code);
        query.executeUpdate();
        //字典
        query = session.createQuery("delete from OrgDict where organization = :code");
        query.setParameter("code", code);
        query.executeUpdate();
        //字典项
        query = session.createQuery("delete from OrgDictItem where organization = :code");
        query.setParameter("code", code);
        query.executeUpdate();
    }

    /**
     * 机构是否存在采集数据
     *
     * @param org
     * @return
     */
    public boolean isExistData(String org) {
        return orgDataSetManager.getMaxSeq(org) + orgMetaDataManager.getMaxSeq(org) + orgDictManager.getMaxSeq(org) + orgDictItemManager.getMaxSeq(org) > 0;
    }

    /**
     * 拷贝
     *
     * @param code
     * @param parent
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean copy(String code, String parent) {
        Session session = currentSession();
        String hql;
        Query query = null;

        //数据集拷贝
        String sql = "insert into org_std_dataset(code,name,create_date,update_date,create_user,update_user,description,organization,sequence)  " +
                "  select t.code,t.name,t.create_date,t.update_date,t.create_user,t.update_user,t.description,:code as organization,t.sequence from org_std_dataset t where t.organization=:parent ";
        Query sqlQuery = session.createSQLQuery(sql);
        sqlQuery.setParameter("code", code);
        sqlQuery.setParameter("parent", parent);
        int rows = sqlQuery.executeUpdate();

        //数据元拷贝
        sql = "insert into org_std_metadata(code,name,create_date,update_date,create_user,update_user,description,organization,sequence,org_dataset,column_type,column_length) " +
                "  select t.code,t.name,t.create_date,t.update_date,t.create_user,t.update_user,t.description,:code as organization,t.sequence,t.org_dataset,t.column_type,t.column_length " +
                "from org_std_metadata t where t.organization=:parent ";
        sqlQuery = session.createSQLQuery(sql);
        sqlQuery.setParameter("code", code);
        sqlQuery.setParameter("parent", parent);
        rows = sqlQuery.executeUpdate();

        //字典拷贝
        sql = "insert into org_std_dict(code,name,create_date,update_date,create_user,update_user,description,organization,sequence) " +
                "  select t.code,t.name,t.create_date,t.update_date,t.create_user,t.update_user,t.description,:code as organization,t.sequence " +
                "from org_std_dict t where t.organization=:parent ";
        sqlQuery = session.createSQLQuery(sql);
        sqlQuery.setParameter("code", code);
        sqlQuery.setParameter("parent", parent);
        rows = sqlQuery.executeUpdate();

        //字典项拷贝
        sql = "insert into org_std_dictentry(code,name,create_date,update_date,create_user,update_user,description,organization,sequence,sort,org_dict) " +
                "  select t.code,t.name,t.create_date,t.update_date,t.create_user,t.update_user,t.description,:code as organization,t.sequence,t.sort,t.org_dict " +
                "from org_std_dictentry t where t.organization=:parent ";
        sqlQuery = session.createSQLQuery(sql);
        sqlQuery.setParameter("code", code);
        sqlQuery.setParameter("parent", parent);
        rows = sqlQuery.executeUpdate();
        return true;
    }
}
