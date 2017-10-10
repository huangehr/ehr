package com.yihu.ehr.org.service;

import com.yihu.ehr.org.dao.XOrganizationRepository;
import com.yihu.ehr.org.feign.GeographyClient;
import com.yihu.ehr.org.model.Organization;
import com.yihu.ehr.query.BaseJpaService;
import org.hibernate.SQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
        if(geographyIds !=null && geographyIds.size() > 0 ){
            List<Organization> orgs = organizationRepository.searchByAddress(geographyIds);
            return orgs;
        }else {
            return null;
        }
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

    public List<Organization> getAllSaasOrgs(String name) {
        List<Organization> codes = organizationRepository.fingorgByFullnameOrShortName(name);
        return codes;
    }

    public List orgCodeExistence(String[] orgCode) {
        String sql = "SELECT org_code FROM organizations WHERE org_code in(:orgCode)";
        SQLQuery sqlQuery = currentSession().createSQLQuery(sql);
        sqlQuery.setParameterList("orgCode", orgCode);
        return sqlQuery.list();
    }

    public String getOrgIdByOrgCode(String orgCode) {
        List<Long> orgId = organizationRepository.getOrgIdByOrgCode(orgCode);
        if (null != orgId && orgId.size() > 0) {
            return Long.toString(orgId.get(0));
        }
        return null;
    }

    public String getOrgCodeByOrgId(Long orgId) {
        return organizationRepository.findOrgCodeByOrgId(orgId);
    }

    public List<String> getOrgCodeByFullName(List<String> fullName) {
        List<String> list = organizationRepository.findOrgCodeByFullName(fullName);
        return list;
    }

    public List<String> getOrgIdByOrgCodeList(List<String> orgCode) {
        List<Long> orgId = organizationRepository.findOrgIdByOrgCodeList(orgCode);
        List<String> orgIdList = new ArrayList<>();
        if (null != orgId && orgId.size() > 0) {
            for (Long l : orgId) {
                orgIdList.add(Long.toString(l));
                return orgIdList;
            }
        }
        return null;
    }

    public List<String> getOrgListById(List<Long> orgId) {
        List<String> list =  organizationRepository.findOrgListById(orgId);
        return list;
    }

}
