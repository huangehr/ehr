package com.yihu.ehr.org.service;

import com.yihu.ehr.model.address.MAddress;
import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.model.security.MUserSecurity;
import com.yihu.ehr.org.feignClient.address.AddressClient;
import com.yihu.ehr.org.feignClient.security.SecurityClient;
import org.apache.commons.lang.time.DateFormatUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 组织机构管理器.
 *
 * @author Sand
 * @version 1.0
 */
@Transactional
@Service
public class OrgManagerService  {

    @Autowired
    AddressClient addressClient;

    @Autowired
    SecurityClient securityClient;


    @Autowired
    private XOrganizationRepository organizationRepository;


    @PersistenceContext
    protected EntityManager entityManager;



    public Object getAddressById(String id){
        Session session = entityManager.unwrap(org.hibernate.Session.class);
        Query query = session.createSQLQuery("SELECT * from organizations where 1=1 ");
        Object model=  query.list().get(0);
        return model;
    }

    public OrgManagerService(){
    }

    public Organization register(String orgCode, String fullName, String shortName){
        Organization org = new Organization();
        org.setCreateDate(new Date());
        org.setOrgCode(orgCode);
        org.setFullName(fullName);
        org.setShortName(shortName);
        organizationRepository.save(org);
        return org;
    }

    public Organization getOrg(String orgCode) {
        Organization org = organizationRepository.getOrgByCode(orgCode);
        return org;
    }

    public List<String> getIdsByName(String name) {
        Session session = entityManager.unwrap(org.hibernate.Session.class);
        Query query = session.createQuery("select org.orgCode from Organization org where org.fullName like :name or org.shortName like :name");
        query.setString("name", "%"+name+"%");
        List<String> ids = query.list();
        return ids;
    }





    public String getOrgStr(String tags){
        return String.join(",", tags);
    }


    /**
     * 转换成viewModel
     * @param org
     * @return
     */
    public OrgModel getOrgModel(String apiVersion,Organization org) {

        OrgModel orgModel = new OrgModel();
        orgModel.setOrgCode(org.getOrgCode());
        orgModel.setFullName(org.getFullName());
        orgModel.setShortName(org.getShortName());
        orgModel.setAdmin(org.getAdmin());
        orgModel.setTel(org.getTel());
        orgModel.setTags(getOrgStr(org.getTags()));
        if (!StringUtils.isEmpty(org.getOrgType())) {
            orgModel.setOrgType(org.getOrgType());
        }
        if (!StringUtils.isEmpty(org.getSettledWay())) {
            orgModel.setSettledWay(org.getSettledWay());
        }
        if (org.getLocation() != null) {
            //这里调用Address服务获取地址
            try {
                MAddress address;
                address = addressClient.getAddressById(apiVersion,org.getLocation());
                orgModel.setProvince(address.getProvince());
                orgModel.setCity(address.getCity());
                orgModel.setDistrict(address.getDistrict());
                orgModel.setTown(address.getTown());
                String addressStr = addressClient.getCanonicalAddress(apiVersion,org.getLocation());
                orgModel.setLocation(addressStr);
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        MUserSecurity userSecurity = securityClient.createSecurityByOrgCode(org.getOrgCode());
        if (userSecurity != null) {
            orgModel.setPublicKey(userSecurity.getPublicKey());
            String validTime = DateFormatUtils.format(userSecurity.getFromDate(),"yyyy-MM-dd")
                    + "~" + DateFormatUtils.format(userSecurity.getExpiryDate(),"yyyy-MM-dd");
            orgModel.setValidTime(validTime);
            orgModel.setStartTime(DateFormatUtils.format(userSecurity.getFromDate(),"yyyy-MM-dd"));
        }

        return orgModel;
    }

    public boolean isRegistered(String orgCode) {
        Organization organization = organizationRepository.getOrgByCode(orgCode);
        return organization != null;
    }

    public void update(Organization org) {
        //这里之前Organization是包含地址的，现在只关联id所以现在只保存机构，在这之前先保存地址服务。
        organizationRepository.save(org);
    }


    public Boolean isExistOrg(String orgCode){
        Organization org = organizationRepository.getOrgByCode(orgCode);
        return org!=null;
    }

    public Boolean update(OrgModel orgModel) {
        Organization org = null;
        if (orgModel.getUpdateFlg().equals("0")) {
            org = register(orgModel.getOrgCode(), orgModel.getFullName(), orgModel.getShortName());
        } else if (orgModel.getUpdateFlg().equals("1")) {
            org = getOrg(orgModel.getOrgCode());
        }
        if (org == null) {
            return false;
        }
        org.setSettledWay(orgModel.getSettledWay());
        org.setOrgType(orgModel.getOrgType());
        org.setShortName(orgModel.getShortName());
        org.setFullName(orgModel.getFullName());
        org.setAdmin(orgModel.getAdmin());
        org.setTel(orgModel.getTel());
        //org.getTags().clear();
        org.addTag(orgModel.getTags());
        MAddress addressModel = new MAddress();
        addressModel.setProvince(orgModel.getProvince());
        addressModel.setCity(orgModel.getCity());
        addressModel.setDistrict(orgModel.getDistrict());
        addressModel.setTown(orgModel.getTown());
        update(org);

        return true;
    }

    public List<Organization> getOrgByOrgType(String orgType) {
        List<Organization> list =  organizationRepository.getOrgByOrgType(orgType);
        return list;
    }

    public int searchCount(String apiVersion,Map<String, Object> args) {

        Session session = entityManager.unwrap(org.hibernate.Session.class);
        String orgCode = (String) args.get("orgCode");
        String fullName = (String) args.get("fullName");
        String settledWay = (String) args.get("settledWay");
        String orgType = (String) args.get("orgType");
        String province = (String) args.get("province");
        String city = (String) args.get("city");
        String district = (String) args.get("district");
        List<String> addressIdList = addressClient.search(apiVersion,province,city,district);


        String hql = "from Organization where (orgCode like :orgCode or fullName like :fullName)";
        if (!StringUtils.isEmpty(settledWay)) {
            hql += " and settledWay = :settledWay";
        }
        if (!StringUtils.isEmpty(orgType)) {
            hql += " and orgType = :orgType";
        }
        if (!StringUtils.isEmpty(province) && !StringUtils.isEmpty(city) &&!StringUtils.isEmpty(district)) {
            hql += " and location in (:addressIdList)";
        }

        Query query = session.createQuery(hql);
        query.setString("orgCode", "%"+orgCode+"%");
        query.setString("fullName", "%"+fullName+"%");
        if (!StringUtils.isEmpty(settledWay)) {
            query.setParameter("settledWay", settledWay);
        }
        if (!StringUtils.isEmpty(orgType)) {
            query.setParameter("orgType", orgType);
        }
        if (!StringUtils.isEmpty(province) && !StringUtils.isEmpty(city) &&!StringUtils.isEmpty(district)) {
            query.setParameterList("addressIdList", addressIdList);
        }

        return query.list().size();
    }


    public List<MOrganization> search(String apiVersion,Map<String, Object> args) {

        Session session = entityManager.unwrap(org.hibernate.Session.class);
        String orgCode = (String) args.get("orgCode");
        String fullName = (String) args.get("fullName");
        String settledWay = (String) args.get("settledWay");
        String orgType = (String) args.get("orgType");
        String province = (String) args.get("province");
        String city = (String) args.get("city");
        String district = (String) args.get("district");

        Integer page = (Integer) args.get("page");
        Integer pageSize = (Integer) args.get("pageSize");

        List<String> addressIdList = addressClient.search(apiVersion,province,city,district);


        String hql = "from Organization where (orgCode like :orgCode or fullName like :fullName)";
        if (!StringUtils.isEmpty(settledWay)) {
            hql += " and settledWay = :settledWay";
        }
        if (!StringUtils.isEmpty(orgType)) {
            hql += " and orgType = :orgType";
        }
        if (!StringUtils.isEmpty(province) && !StringUtils.isEmpty(city) &&!StringUtils.isEmpty(district)) {
            hql += " and location in (:addressIdList)";
        }

        Query query = session.createQuery(hql);
        query.setString("orgCode", "%"+orgCode+"%");
        query.setString("fullName", "%"+fullName+"%");
        if (!StringUtils.isEmpty(settledWay)) {
            query.setParameter("settledWay", settledWay);
        }
        if (!StringUtils.isEmpty(orgType)) {
            query.setParameter("orgType", orgType);
        }
        if (!StringUtils.isEmpty(province) && !StringUtils.isEmpty(city) &&!StringUtils.isEmpty(district)) {
            query.setParameterList("addressIdList", addressIdList);
        }

        query.setMaxResults(pageSize);
        query.setFirstResult((page - 1) * pageSize);

        return query.list();
    }


    public List<MOrganization> searchOrgDetailModel(String apiVersion,Map<String, Object> args) {
        List<MOrganization> orgList = search(apiVersion,args);
        return orgList;
    }



    public void delete(String orgCode){
        if(orgCode != null){
            Organization organization = getOrg(orgCode);
            if(organization!=null){
                organizationRepository.delete(organization);
            }

        }
    }

    public List<Organization> searchByAddress(String apiVersion,String province, String city) {
        Session session = entityManager.unwrap(org.hibernate.Session.class);
        List<String> addressIds = addressClient.search(apiVersion,province,city,"");
        String hql = "from Organization where location in (:addressIds)";
        Query query = session.createQuery(hql);
        query.setParameterList("addressIds", addressIds);
        return query.list();
    }


    public String saveAddress(String apiVersion,MAddress location) {
        String country = location.getCountry();
        String province = location.getProvince()!=null ? location.getProvince() :"";
        String city = location.getCity()!=null ? location.getCity() :"";
        String district = location.getDistrict()!=null ? location.getDistrict() :"";
        String town = location.getTown()!=null ? location.getTown() :"";
        String street = location.getStreet()!=null ? location.getStreet() :"";
        String extra = location.getExtra()!=null ? location.getExtra() :"";
        String postalCode = location.getPostalCode()!=null ? location.getPostalCode() :"";
        return addressClient.saveAddress(apiVersion,country,province,city,district,town,street,extra,postalCode);
    }


}
