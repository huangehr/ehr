package com.yihu.ehr.basic.org.service;

import com.yihu.ehr.basic.address.service.AddressService;
import com.yihu.ehr.basic.org.dao.OrganizationRepository;
import com.yihu.ehr.basic.org.model.OrgDept;
import com.yihu.ehr.basic.org.model.Organization;
import com.yihu.ehr.entity.address.Address;
import com.yihu.ehr.model.org.MOrgDeptJson;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.util.id.BizObject;
import com.yihu.ehr.util.id.ObjectId;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 组织机构管理器.
 *
 * @author Sand
 * @version 1.0
 */
@Service
@Transactional
public class OrgService extends BaseJpaService<Organization, OrganizationRepository> {

    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private AddressService addressService;
    @Autowired
    private OrgDeptService orgDeptService;

    @Value("${deploy.region}")
    Short deployRegion = 3502;

    public OrgService() {
    }

    public Organization getOrg(String orgCode) {
        List<Organization> list = organizationRepository.findOrgByCode(orgCode);
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public Organization getOrgById(String orgId) {
        List<Organization> list = organizationRepository.findOrgById(Long.valueOf(orgId));
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public Organization getOrgByAdminLoginCode(String orgCode, String adminLoginCode) {
        List<Organization> list = organizationRepository.findByOrgAdmin(orgCode, adminLoginCode);
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }


//    public Organization saveOrg(Organization org) {
//        return save(org);
//    }


    public Boolean isExistOrg(String orgCode) {
        Organization org = organizationRepository.findOne(orgCode);
        return org != null;
    }

    public Boolean checkSunOrg(String orgPid, String orgId) {
        List<Organization> orgs = organizationRepository.checkSunOrg(Integer.valueOf(orgPid), Long.valueOf(orgId));
        if (orgs != null && orgs.size() > 0) {
            return true;
        }
        return false;
    }


    public void delete(String orgCode) {
        organizationRepository.delete(orgCode);
    }

    public List<Organization> searchByAddress(String province, String city, String district) {
        List<String> geographyIds = addressService.search(province, city, district);
        if (geographyIds != null && geographyIds.size() > 0) {
            List<Organization> orgs = organizationRepository.searchByAddress(geographyIds);
            return orgs;
        } else {
            return new ArrayList<>();
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
        List<String> list = organizationRepository.findOrgListById(orgId);
        return list;
    }

    protected String getObjectId(BizObject bizObject) {
        return new ObjectId(deployRegion, bizObject).toString();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean addOrgBatch(List<Map<String, Object>> orgDepts) {
        Map<String, Object> map;
        Address geography = null;
        List<Address> geographies = null;
        try {
            for (int i = 1; i <= orgDepts.size(); i++) {
                map = orgDepts.get(i - 1);
                String addressId = "";
                //保存地址到addresses---start
                geography = new Address();
                if (geography.getCountry() == null) {
                    geography.setCountry("中国");
                }
                Map<String, String> adressMap = getAdressByLocation(map.get("location").toString());
                geography.setProvince(adressMap.get("provinceName").toString());
                geography.setCity(adressMap.get("cityName").toString());
                geography.setDistrict(adressMap.get("district").toString());
                geography.setTown(adressMap.get("town").toString());
                geography.setStreet(adressMap.get("street").toString());
                geographies = addressService.isGeographyExist(geography);
                if (geographies == null || geographies.size() == 0) {
                    geography.setId(getObjectId(BizObject.Geography));
                    addressId = addressService.saveAddress(geography);
                } else {
                    addressId = geographies.get(0).getId();
                }
                //保存地址到addresses---end
                Organization organization = new Organization();
//                organization.setId(Long.parseLong(map.get("id").toString()));
                organization.setFullName(null2Space(map.get("fullName")));
                organization.setBasicUnitFlag(null2Space(map.get("basicUnitFlag")));
                organization.setParentHosId(Integer.valueOf(null2Space(map.get("parentHosId"))));
                organization.setActivityFlag(Integer.valueOf(null2Space(map.get("activityFlag"))));

                organization.setOrgChanges(null2Space(map.get("orgChanges")));
                organization.setOrgCode(null2Space(map.get("orgCode")));
                organization.setHosManageType(null2Space(map.get("hosManageType")));
                organization.setHosEconomic(null2Space(map.get("hosEconomic")));
                organization.setHosTypeId(null2Space(map.get("hosTypeId")));

                organization.setHosTypeName(null2Space(map.get("hosTypeName")));
                organization.setAdministrativeDivision(Integer.valueOf(null2Space(map.get("administrativeDivision"))));
                organization.setStreetId(null2Space(map.get("streetId")));
                organization.setLevelId(null2Space(map.get("levelId")));
                organization.setHosHierarchy(null2Space(map.get("hosHierarchy")));

                organization.setHostUnit(null2Space(map.get("hostUnit")));
                organization.setAscriptionType(Integer.valueOf(null2Space(map.get("ascriptionType"))));
                organization.setDischargePatientFlag(null2Space(map.get("dischargePatientFlag")));
                organization.setReportingClinicFlag(null2Space(map.get("reportingClinicFlag")));
                organization.setReportingVillageClinicFlag(null2Space(map.get("reportingVillageClinicFlag")));

                organization.setReportingOrg(null2Space(map.get("reportingOrg")));
                organization.setFoundingTime(null2Space(map.get("foundingTime")));
                organization.setRegisteredCapital(null2Space(map.get("registeredCapital")));
                organization.setLegalPerson(null2Space(map.get("legalPerson")));
                organization.setBranchOrgFlag(null2Space(map.get("branchOrgFlag")));
                organization.setLocation(addressId);
                organization.setPostalcode(null2Space(map.get("postalcode")));
                organization.setTel(null2Space(map.get("tel")));
                organization.setEmail(null2Space(map.get("email")));
                organization.setDomainName(null2Space(map.get("domainName")));

                organization.setRegistrationNumber(null2Space(map.get("registrationNumber")));
                organization.setRegistrationRatificationAgency(null2Space(map.get("registrationRatificationAgency")));
                organization.setCertificateDate(null2Space(map.get("certificateDate")));
                organization.setOperator(null2Space(map.get("operator")));
                organization.setEntryStaff(null2Space(map.get("entryStaff")));
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                organization.setCreateTime(df.format(new Date()));
                organization.setCancelTime(null2Space(map.get("cancelTime")));

                organization.setUpdateTime(df.parse(null2Space(map.get("updateTime"))));
                organization.setTermValidityStart(null2Space(map.get("termValidityStart")));
                organization.setTermValidityEnd(null2Space(map.get("termValidityEnd")));
                organization.setCreateDate(new Date());
                organization.setJkzlOrgId(null2Space(map.get("jkzlOrgId")));
                //未提供核定床位，默认0
                organization.setBerth(0);
                /****************2018年3月27日  新增和总部同步字段 结束*******************/

                Organization org = organizationRepository.save(organization);
                List<Long> orgList = organizationRepository.getOrgIdByOrgCode(organization.getOrgCode());
                Long Id = new Long(1);
                if (orgList.size() > 0) {
                    for (Long o : orgList) {
                        Id = o;
                    }
                }
                OrgDept dept = new OrgDept();
                dept.setOrgId(String.valueOf(Id));
                dept.setCode(String.valueOf(Id) + "1");
                dept.setName("未分配");
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
    public List orgExist(String[] org_codes) {
        String sql = "SELECT org_code, full_name FROM organizations WHERE org_code in(:org_codes)";
        SQLQuery sqlQuery = currentSession().createSQLQuery(sql);
        sqlQuery.setParameterList("org_codes", org_codes);
        return sqlQuery.list();
    }

    public List<Organization> getHospital() {
        List<Organization> list = organizationRepository.getHospital();
        return list;
    }

    public List<Organization> getOrgListByAddressPid(Integer pid) {
        List<Organization> list = organizationRepository.getOrgListByAddressPid(pid);
        return list;
    }

    public List<Organization> getOrgListByAddressPidAndParam(Integer pid, String fullName) {
        List<Organization> list = organizationRepository.getOrgListByAddressPidAndParam(pid, fullName);
        return list;
    }

    public List<Organization> getOrgListByAddressPidAndOrgArea(Integer pid, String orgArea) {
        List<Organization> list = organizationRepository.getOrgListByAddressPidAndOrgArea(pid, orgArea);
        return list;
    }

    /**
     * 根据用户id获取医生所在的机构id
     *
     * @param userId
     * @return
     */
    public List getEhrOrgIdsByUserId(String userId) {
        String sql = "SELECT d.org_id FROM doctors d  JOIN  users u WHERE d.id = u.doctor_id AND u.id = :userId";
        SQLQuery sqlQuery = currentSession().createSQLQuery(sql);
        sqlQuery.setParameter("userId", userId);
        return sqlQuery.list();
    }

    public List getJkzlOrgIdsByEhrOrgId(String[] orgIds) {
        String sql = "SELECT jkzl_org_id FROM organizations WHERE id in(:orgIds) AND  jkzl_org_id IS NOT NULL";
        SQLQuery sqlQuery = currentSession().createSQLQuery(sql);
        sqlQuery.setParameterList("orgIds", orgIds);
        return sqlQuery.list();
    }

    public List<String> getOrgList(List<MOrgDeptJson> orgDeptJsonList) {
        List<Long> orgId = new ArrayList<>();
        for (MOrgDeptJson org : orgDeptJsonList) {
            orgId.add(Long.parseLong(org.getOrgId()));
        }
        if (orgId.size() > 0) {
            List<String> orgList = organizationRepository.findOrgListById(orgId);
            return orgList;
        }
        return null;
    }


    /**
     * 查询所有机构的组织机构代码和全称
     */
    public List getOrgCodeAndFullName(String field) {
        String sql = "";
        if (field.equals("id")) {
            sql = "SELECT org_code,id FROM organizations";
        } else {
            sql = "SELECT org_code,full_name FROM organizations";
        }
        SQLQuery sqlQuery = currentSession().createSQLQuery(sql);
        return sqlQuery.list();
    }

    /**
     * 根据机构的地址拆分保存
     */
    public Map<String, String> getAdressByLocation(String location) {
        Map<String, String> map = new HashMap<>();
        map.put("provinceName", "");
        map.put("cityName", "");
        map.put("district", "");
        map.put("street", "");
        map.put("town", "");
        String[] temp = null;
        temp = location.split("省");
        String str = "";
        for (int i = 0; i < temp.length; i++) {
            if (temp[0].indexOf("省") > 0) {
                //不包括直辖市、自治区
                map.put("provinceName", temp[0] + "省");
            }
            str = temp[temp.length - 1];
        }
        if (StringUtils.isNotEmpty(str)) {
            temp = str.split("市");
            str = temp[temp.length - 1];
            if (temp.length > 2) {
                map.put("cityName", temp[0] + "市");
                map.put("district", temp[1] + "市");
                map.put("street", temp[2]);
            } else if (temp.length > 1) {
                map.put("cityName", temp[0] + "市");
                map.put("district", temp[1]);
                temp = str.split("县");
                if (temp.length > 1) {
                    map.put("district", temp[0] + "县");
                    map.put("street", temp[1]);
                } else {
                    temp = str.split("区");
                    if (temp.length > 1) {
                        map.put("district", temp[0] + "区");
                        map.put("street", temp[1]);
                    } else {
                        map.put("street", temp[0]);
                    }
                }
            } else {
                temp = str.split("县");
                if (temp.length > 1) {
                    map.put("district", temp[0] + "县");
                    map.put("street", temp[1]);
                } else {
                    temp = str.split("区");
                    if (temp.length > 1) {
                        map.put("district", temp[0] + "区");
                        map.put("street", temp[1]);
                    } else {
                        map.put("street", temp[0]);
                    }
                }
            }
        }
        return map;
    }

    private String null2Space(Object o) {
        return o == null ? "" : o.toString();
    }
}
