package com.yihu.ehr.org.service;

import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.entity.geography.Geography;
import com.yihu.ehr.geography.service.GeographyService;
import com.yihu.ehr.org.dao.XOrganizationRepository;
import com.yihu.ehr.org.feign.GeographyClient;
import com.yihu.ehr.org.model.OrgDept;
import com.yihu.ehr.org.model.Organization;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.util.id.ObjectId;
import org.hibernate.SQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    @Autowired
    private GeographyService geographyService;
    @Autowired
    private OrgDeptService orgDeptService;

    @Value("${deploy.region}")
    Short deployRegion = 3502;

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

    protected String getObjectId(BizObject bizObject){
        return new ObjectId(deployRegion, bizObject).toString();
    }
    @Transactional(propagation = Propagation.REQUIRED)

    public boolean addOrgBatch(List<Map<String, Object>> orgDepts) {
        Map<String, Object> map;
        Geography geography =null;
        List<Geography> geographies =null;
      try{
        for(int i=1; i <= orgDepts.size(); i++){
            map = orgDepts.get(i-1);
            String addressId ="";
            //保存地址到addresses---start
            geography = new Geography();
            if (geography.getCountry() == null) {
                geography.setCountry("中国");
            }
            geography.setProvince(map .get("provinceName").toString());
            geography.setCity(map .get("cityName").toString());
            geography.setDistrict(map .get("district").toString());
            geography.setTown(map .get("town").toString());
            geography.setStreet(map .get("street").toString());
            geographies=geographyService.isGeographyExist(geography);
            if(geographies==null || geographies.size()==0){
                geography.setId(getObjectId(BizObject.Geography));
                addressId=geographyService.saveAddress(geography);
            }else{
                addressId= geographies.get(0).getId();
            }
            //保存地址到addresses---end
            Organization organization=new Organization();
            organization.setOrgCode(map .get("orgCode").toString());
            organization.setFullName(map .get("fullName").toString());
            organization.setShortName(map .get("shortName").toString());
            organization.setLevelId(map .get("levelId").toString());
            organization.setLegalPerson(map .get("legalPerson").toString());
            organization.setAdmin(map .get("admin").toString());
            organization.setPhone(map .get("phone").toString());
            organization.setTel(map .get("phone").toString());
            if(null!=map .get("zxy")){
                organization.setZxy(Integer.valueOf(map .get("zxy").toString()));//中西医标识
            }
            if(null!=map .get("hosTypeId")){
                organization.setHosTypeId(map .get("hosTypeId").toString());///
            }
            if(null!=map .get("orgType")){
                organization.setOrgType(map .get("orgType").toString());
            }
            if(null!=map .get("settledWay")){
                organization.setSettledWay(map .get("settledWay").toString());
            }

            if(null!=map .get("ascriptionType")){
                organization.setAscriptionType(Integer.valueOf(map .get("ascriptionType").toString()));
            }

            organization.setLocation(addressId);
            organization.setTraffic(map .get("traffic").toString());

            organization.setIng(map .get("ing").toString());
            organization.setLat(map .get("lat").toString());
            organization.setCreateDate(new Timestamp(new Date().getTime()));
            organization.setAdministrativeDivision(Integer.valueOf(map .get("administrativeDivision").toString()));
            organization.setActivityFlag(1);
            organization.setIntroduction(map .get("introduction").toString());
            Integer berth=0;
            if(null!=map .get("berth")){
                berth=Integer.valueOf( map .get("berth").toString());
            }
            organization.setBerth(berth);
            Organization org=organizationRepository.save(organization);
            List<Long> orgList= organizationRepository.getOrgIdByOrgCode(organization.getOrgCode());
            Long Id=new Long(1);
            if(orgList.size()>0){
                for(Long o:orgList){
                    Id=o;
                }
            }
            OrgDept dept=new OrgDept();
            dept.setOrgId(String.valueOf(Id));
            dept.setCode(String.valueOf(Id)+"1");
            dept.setName("未分配部门人员");
            orgDeptService.saveOrgDept(dept);
            }
           } catch (Exception e) {
             e.printStackTrace();
          return false;
          }
        return true;
    }

    /**
     * 查询机构是否已存在， 返回已存在机构code、name
     */
    public List orgExist(String[] org_codes)
    {
        String sql = "SELECT org_code, full_name FROM organizations WHERE org_code in(:org_codes)";
        SQLQuery sqlQuery = currentSession().createSQLQuery(sql);
        sqlQuery.setParameterList("org_codes", org_codes);
        return sqlQuery.list();
    }

    public List<Organization> getHospital() {
        List<Organization> list =  organizationRepository.getHospital();
        return list;
    }
}
