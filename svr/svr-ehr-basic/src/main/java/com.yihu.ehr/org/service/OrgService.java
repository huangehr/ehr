package com.yihu.ehr.org.service;

import com.yihu.ehr.org.dao.XOrganizationRepository;
import com.yihu.ehr.org.feign.GeographyClient;
import com.yihu.ehr.org.model.Organization;
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
        List<Organization> list =  organizationRepository.findOrgByCode(orgCode);
        if (list.size()>0){
            return list.get(0);
        }else {
            return null;
        }
    }

    public Organization getOrgById(String orgId) {
        List<Organization> list =  organizationRepository.findOrgById(Long.valueOf(orgId));
        if (list.size()>0){
            return list.get(0);
        }else {
            return null;
        }
    }

    public Organization getOrgByAdminLoginCode(String orgCode,String adminLoginCode) {
        List<Organization> list =  organizationRepository.findByOrgAdmin(orgCode,adminLoginCode);
        if (list.size()>0){
            return list.get(0);
        }else {
            return null;
        }
    }


//    public Organization saveOrg(Organization org) {
//        return save(org);
//    }


    public Boolean isExistOrg(String orgCode){
        Organization org = organizationRepository.findOne(orgCode);
        return org!=null;
    }

    public Boolean checkSunOrg(String orgPid,String orgId){
        List<Organization> orgs = organizationRepository.checkSunOrg(Integer.valueOf(orgPid), Long.valueOf(orgId));
        if(orgs!=null && orgs.size() >0){
            return  true;
        }
        return false;
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


    public List<Organization> findByOrgCodes(List<String> orgCodes) {
        return organizationRepository.findByOrgCodes(orgCodes);
    }

    public List<Organization> findByOrgArea(String area) {
        return organizationRepository.findByArea(area + "%");
    }
}
