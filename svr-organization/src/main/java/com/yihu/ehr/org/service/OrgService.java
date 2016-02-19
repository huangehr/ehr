package com.yihu.ehr.org.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.model.address.MGeography;
import com.yihu.ehr.org.feign.GeographyClient;
import com.yihu.ehr.query.BaseJpaService;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * 组织机构管理器.
 *
 * @author Sand
 * @version 1.0
 */
@Service
@Transactional
public class OrgService extends BaseJpaService<Organization, XOrganizationRepository> {

    @Autowired
    private XOrganizationRepository organizationRepository;
    @Autowired
    private GeographyClient geographyClient;
    @PersistenceContext
    private EntityManager entityManager;


    public OrgService() {
    }

    public Organization getOrg(String orgCode) {
        return organizationRepository.findOne(orgCode);
    }


    public void save(Organization org) {
        organizationRepository.save(org);
    }


    public Boolean isExistOrg(String orgCode){
        Organization org = organizationRepository.findOne(orgCode);
        return org!=null;
    }


    public void delete(String orgCode){
        organizationRepository.delete(orgCode);
    }

    public List<Organization> searchByAddress(String province, String city,String district) {
        Session session = entityManager.unwrap(org.hibernate.Session.class);
        List<String> geographyIds = geographyClient.search(province,city,district);
        String hql = "from Organization where location in (:geographyIds)";
        Query query = session.createQuery(hql);
        query.setParameterList("geographyIds", geographyIds);
        return query.list();
    }


    public List<String> getIdsByName(String name) {
        Session session = entityManager.unwrap(org.hibernate.Session.class);
        Query query = session.createQuery("select org.orgCode from Organization org where org.fullName like :name or org.shortName like :name");
        query.setString("name", "%"+name+"%");
        List<String> ids = query.list();
        return ids;
    }


}
