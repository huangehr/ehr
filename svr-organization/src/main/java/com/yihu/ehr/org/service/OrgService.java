package com.yihu.ehr.org.service;

import com.yihu.ehr.model.address.MAddress;
import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.model.security.MUserSecurity;
import com.yihu.ehr.org.feign.ConventionalDictClient;
import com.yihu.ehr.org.feign.GeographyClient;
import com.yihu.ehr.org.feign.SecurityClient;
import org.apache.commons.lang.time.DateFormatUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
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
public class OrgService {

    @Autowired
    GeographyClient addressClient;

    @Autowired
    SecurityClient securityClient;

    @Autowired
    private ConventionalDictClient conventionalDictClient;

    @Autowired
    private XOrganizationRepository organizationRepository;


    @PersistenceContext
    protected EntityManager entityManager;


    public Organization getOrg(String orgCode) {
        return organizationRepository.findOne(orgCode);
    }

    public List<String> getIdsByName(String name) {
        Session session = entityManager.unwrap(org.hibernate.Session.class);
        Query query = session.createQuery("select org.orgCode from Organization org where org.fullName like :name or org.shortName like :name");
        query.setString("name", "%"+name+"%");
        List<String> ids = query.list();
        return ids;
    }



    public void save(Organization org) {
        organizationRepository.save(org);
    }


    public Boolean isExistOrg(String orgCode){
        Organization org = organizationRepository.findOne(orgCode);
        return org!=null;
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



    public void delete(String orgCode){
        organizationRepository.delete(orgCode);
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
