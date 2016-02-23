package com.yihu.ehr.org.service;

import com.yihu.ehr.org.feign.GeographyClient;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        List<String> geographyIds = geographyClient.search(province,city,district);
        List<Organization> orgs = organizationRepository.searchByAddress(geographyIds);
        return orgs;
    }


    public List<String> getCodesByName(String name) {
        List<String> codes = organizationRepository.fingIdsByFullnameOrShortName(name);
        return codes;
    }


}
