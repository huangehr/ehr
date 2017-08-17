package com.yihu.ehr.patient.service.demographic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.model.geography.MGeography;
import com.yihu.ehr.patient.dao.XDemographicInfoRepository;
import com.yihu.ehr.patient.feign.GeographyClient;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import com.yihu.ehr.util.hash.HashUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

/**
 * 人口学索引实现类.
 *
 * @author Sand
 * @version 1.0
 * @created 04-6月-2015 19:53:04
 */
@Transactional
@Service
public class DemographicService {

    @PersistenceContext
    protected EntityManager entityManager;

    @Autowired
    private XDemographicInfoRepository demographicInfoRepository;
    @Autowired
    private GeographyClient addressClient;

    public DemographicService() {
    }


    public DemographicInfo getDemographicInfo(DemographicId id) {
        DemographicInfo demInfo = demographicInfoRepository.findOne(id);
        return demInfo;
    }

    public DemographicInfo getDemographicInfoBytelephoneNo(String telephoneNo) {
        //{"联系电话":"15965368965"}
        telephoneNo="{\"联系电话\":\""+telephoneNo+"\"}";
        List<DemographicInfo> demInfoList = demographicInfoRepository.getDemographicInfoByTelephoneNo(telephoneNo);
        DemographicInfo demInfo=null;
        if(null!=demInfoList&&demInfoList.size()>0){
            demInfo=demInfoList.get(0);
        }
        return demInfo;
    }




    public void save(DemographicInfo demographicInfo) throws JsonProcessingException {
        demographicInfoRepository.save(demographicInfo);
    }


    public boolean savePatient(DemographicInfo demographicInfo) throws Exception{
        save(demographicInfo);
        return true;
    }

    public List<DemographicInfo> searchPatient(Map<String, Object> args) {
        Session session = entityManager.unwrap(org.hibernate.Session.class);
        String search = (String) args.get("search");
        Integer page = (Integer) args.get("page");
        Integer pageSize = (Integer) args.get("pageSize");

        String province = (String) args.get("province");
        String city = (String) args.get("city");
        String district = (String) args.get("district");
        boolean addressNotNull=(!StringUtils.isEmpty(province) || !StringUtils.isEmpty(city) || !StringUtils.isEmpty(district));
        List<String> homeAddressIdList = null;
        String hql = "from DemographicInfo where 1=1";
        if (!StringUtils.isEmpty(search)) {
            hql += " and ((id like :search) or (name like :search))";
        }
        if (addressNotNull) {
            homeAddressIdList = addressClient.search(province,city,district);
            hql += " and homeAddress in (:homeAddressIdList)";
        }
        hql += " order by registerTime desc";
        Query query = session.createQuery(hql);
        if (!StringUtils.isEmpty(search)) {
            query.setString("search", "%" + search + "%");
        }
        if (addressNotNull) {
            query.setParameterList("homeAddressIdList", homeAddressIdList);
        }
        query.setMaxResults(pageSize);
        query.setFirstResult((page - 1) * pageSize);
        List<DemographicInfo> demographicInfos = query.list();
        return demographicInfos;
    }

    public Integer searchPatientTotalCount(Map<String, Object> args) {
        Session session = entityManager.unwrap(org.hibernate.Session.class);
        String search = (String) args.get("search");

        String province = (String) args.get("province");
        String city = (String) args.get("city");
        String district = (String) args.get("district");
        boolean addressNotNull=(!StringUtils.isEmpty(province) || !StringUtils.isEmpty(city) || !StringUtils.isEmpty(district));
        List<String> homeAddressIdList = null;
        String hql = "select count(*) from DemographicInfo where 1=1";
        if (!StringUtils.isEmpty(search)) {
            hql += " and ((id like :search) or (name like :search))";
        }
        if (addressNotNull) {
            homeAddressIdList = addressClient.search(province,city,district);
            hql += " and homeAddress in (:homeAddressIdList)";
        }
        Query query = session.createQuery(hql);
        if (!StringUtils.isEmpty(search)) {
            query.setString("search", "%" + search + "%");
        }
        if (addressNotNull) {
            query.setParameterList("homeAddressIdList", homeAddressIdList);
        }
        return ((Long)query.list().get(0)).intValue();
    }

    public Boolean isNullAddress(MGeography address) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String geographyModelJsonData = objectMapper.writeValueAsString(address);
        return addressClient.isNullAddress(geographyModelJsonData);
    }


    public String savaAddress(MGeography address) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String GeographyModelJsonData = objectMapper.writeValueAsString(address);
        return addressClient.saveAddress(GeographyModelJsonData);
    }



    public void delete(DemographicId id) {
        DemographicInfo di = (DemographicInfo) getDemographicInfo(id);
        demographicInfoRepository.delete(di);
    }

    public void resetPass(DemographicId id) {

        String pwd = "123456";
        DemographicInfo demInfo = getDemographicInfo(id);
        demInfo.setPassword(HashUtil.hash(pwd));
        demographicInfoRepository.save(demInfo);
    }
    public List<DemographicInfo> searchPatientByParams(Map<String, Object> args) {
        Session session = entityManager.unwrap(org.hibernate.Session.class);
        String search = (String) args.get("search");
        Integer page = (Integer) args.get("page");
        Integer pageSize = (Integer) args.get("pageSize");

        String province = (String) args.get("province");
        String city = (String) args.get("city");
        String district = (String) args.get("district");
        String gender = (String) args.get("gender");

        Date searchRegisterTimeStart = (Date) args.get("startDate");
        Date searchRegisterTimeEnd = (Date) args.get("endDate");

        boolean addressNotNull=(!StringUtils.isEmpty(province) || !StringUtils.isEmpty(city) || !StringUtils.isEmpty(district));
        List<String> homeAddressIdList = null;
        String hql = "from DemographicInfo where 1=1";
        if (!StringUtils.isEmpty(search)) {
            hql += " and ((id like :search) or (name like :search))";
        }
        if (!StringUtils.isEmpty(gender)) {
            hql += " and gender = (:gender)";
        }
        if (addressNotNull) {
            homeAddressIdList = addressClient.search(province,city,district);
            hql += " and homeAddress in (:homeAddressIdList)";
        }

        if (!StringUtils.isEmpty(searchRegisterTimeStart)) {
            hql += " and registerTime>= :searchRegisterTimeStart";
        }
        if (!StringUtils.isEmpty(searchRegisterTimeEnd)) {
            hql += " and registerTime < :searchRegisterTimeEnd";
        }
        hql += " order by registerTime desc";
        Query query = session.createQuery(hql);
        if (!StringUtils.isEmpty(search)) {
            query.setString("search", "%" + search + "%");
        }
        if (!StringUtils.isEmpty(gender)) {
            query.setString("gender", gender);
        }
        if (addressNotNull) {
            query.setParameterList("homeAddressIdList", homeAddressIdList);
        }
        if (!StringUtils.isEmpty(searchRegisterTimeStart)) {
            query.setDate("searchRegisterTimeStart",searchRegisterTimeStart);
        }
        if (!StringUtils.isEmpty(searchRegisterTimeEnd)) {
            query.setDate("searchRegisterTimeEnd", searchRegisterTimeEnd);
        }
        query.setMaxResults(pageSize);
        query.setFirstResult((page - 1) * pageSize);
        List<DemographicInfo> demographicInfos = query.list();
        return demographicInfos;
    }

    public Integer searchPatientByParamsTotalCount(Map<String, Object> args) {
        Session session = entityManager.unwrap(org.hibernate.Session.class);
        String search = (String) args.get("search");

        String province = (String) args.get("province");
        String city = (String) args.get("city");
        String district = (String) args.get("district");
        String gender = (String) args.get("gender");
        Date searchRegisterTimeStart = (Date) args.get("startDate");
        Date searchRegisterTimeEnd = (Date) args.get("endDate");
        boolean addressNotNull=(!StringUtils.isEmpty(province) || !StringUtils.isEmpty(city) || !StringUtils.isEmpty(district));
        List<String> homeAddressIdList = null;
        String hql = "select count(*) from DemographicInfo where 1=1";
        if (!StringUtils.isEmpty(search)) {
            hql += " and ((id like :search) or (name like :search))";
        }
        if (!StringUtils.isEmpty(gender)) {
            hql += " and gender = (:gender)";
        }
        if (addressNotNull) {
            homeAddressIdList = addressClient.search(province,city,district);
            hql += " and homeAddress in (:homeAddressIdList)";
        }

        if (!StringUtils.isEmpty(searchRegisterTimeStart)) {
            hql += " and registerTime >= :searchRegisterTimeStart";
        }
        if (!StringUtils.isEmpty(searchRegisterTimeEnd)) {
            hql += " and registerTime < :searchRegisterTimeEnd";
        }
        Query query = session.createQuery(hql);
        if (!StringUtils.isEmpty(search)) {
            query.setString("search", "%" + search + "%");
        }
        if (!StringUtils.isEmpty(gender)) {
            query.setString("gender", gender);
        }
        if (addressNotNull) {
            query.setParameterList("homeAddressIdList", homeAddressIdList);
        }
        if (!StringUtils.isEmpty(searchRegisterTimeStart)) {
            query.setDate("searchRegisterTimeStart", searchRegisterTimeStart);
        }
        if (!StringUtils.isEmpty(searchRegisterTimeEnd)) {
            query.setDate("searchRegisterTimeEnd", searchRegisterTimeEnd);
        }
        return ((Long)query.list().get(0)).intValue();
    }

    public List<DemographicInfo> searchPatientByParams2(Map<String, Object> args) {
        Session session = entityManager.unwrap(org.hibernate.Session.class);
        String search = (String) args.get("search");
        Integer page = (Integer) args.get("page");
        Integer pageSize = (Integer) args.get("pageSize");

        String province = (String) args.get("province");
        String city = (String) args.get("city");
        String district = (String) args.get("district");
        String gender = (String) args.get("gender");
        String districtList = (String) args.get("districtList");
        List<String> locationList = stringToList(districtList);

        Date searchRegisterTimeStart = (Date) args.get("startDate");
        Date searchRegisterTimeEnd = (Date) args.get("endDate");

        boolean addressNotNull=(!StringUtils.isEmpty(province) || !StringUtils.isEmpty(city) || !StringUtils.isEmpty(district));
        List<String> homeAddressIdList = null;
        String hql = "from DemographicInfo where 1=1";
        if (!StringUtils.isEmpty(search)) {
            hql += " and ((id like :search) or (name like :search))";
        }
        if (!StringUtils.isEmpty(gender)) {
            hql += " and gender = (:gender)";
        }
        if (addressNotNull) {
            homeAddressIdList = addressClient.search(province,city,district);
            hql += " and homeAddress in (:homeAddressIdList)";
        }
        if (!StringUtils.isEmpty(districtList)) {
            hql += " and homeAddress in (:locationList)";
        } else {
            hql += " and homeAddress in ('-1')";
        }

        if (!StringUtils.isEmpty(searchRegisterTimeStart)) {
            hql += " and registerTime>= :searchRegisterTimeStart";
        }
        if (!StringUtils.isEmpty(searchRegisterTimeEnd)) {
            hql += " and registerTime < :searchRegisterTimeEnd";
        }
        hql += " order by registerTime desc";
        Query query = session.createQuery(hql);
        if (!StringUtils.isEmpty(search)) {
            query.setString("search", "%" + search + "%");
        }
        if (!StringUtils.isEmpty(gender)) {
            query.setString("gender", gender);
        }
        if (addressNotNull) {
            query.setParameterList("homeAddressIdList", homeAddressIdList);
        }
        if (!StringUtils.isEmpty(districtList)) {
            query.setParameterList("locationList", locationList);
        }
        if (!StringUtils.isEmpty(searchRegisterTimeStart)) {
            query.setDate("searchRegisterTimeStart",searchRegisterTimeStart);
        }
        if (!StringUtils.isEmpty(searchRegisterTimeEnd)) {
            query.setDate("searchRegisterTimeEnd", searchRegisterTimeEnd);
        }
        query.setMaxResults(pageSize);
        query.setFirstResult((page - 1) * pageSize);
        List<DemographicInfo> demographicInfos = query.list();
        return demographicInfos;
    }

    public Integer searchPatientByParamsTotalCount2(Map<String, Object> args) {
        Session session = entityManager.unwrap(org.hibernate.Session.class);
        String search = (String) args.get("search");

        String province = (String) args.get("province");
        String city = (String) args.get("city");
        String district = (String) args.get("district");
        String gender = (String) args.get("gender");
        String districtList = (String) args.get("districtList");
        Date searchRegisterTimeStart = (Date) args.get("startDate");
        Date searchRegisterTimeEnd = (Date) args.get("endDate");
        List<String> locationList = stringToList(districtList);
        boolean addressNotNull=(!StringUtils.isEmpty(province) || !StringUtils.isEmpty(city) || !StringUtils.isEmpty(district));
        List<String> homeAddressIdList = null;
        String hql = "select count(*) from DemographicInfo where 1=1";
        if (!StringUtils.isEmpty(search)) {
            hql += " and ((id like :search) or (name like :search))";
        }
        if (!StringUtils.isEmpty(gender)) {
            hql += " and gender = (:gender)";
        }
        if (addressNotNull) {
            homeAddressIdList = addressClient.search(province,city,district);
            hql += " and homeAddress in (:homeAddressIdList)";
        }

        if (!StringUtils.isEmpty(districtList)) {
            hql += " and homeAddress in (:locationList)";
        } else {
            hql += " and homeAddress in ('-1')";
        }

        if (!StringUtils.isEmpty(searchRegisterTimeStart)) {
            hql += " and registerTime >= :searchRegisterTimeStart";
        }
        if (!StringUtils.isEmpty(searchRegisterTimeEnd)) {
            hql += " and registerTime < :searchRegisterTimeEnd";
        }
        Query query = session.createQuery(hql);
        if (!StringUtils.isEmpty(search)) {
            query.setString("search", "%" + search + "%");
        }
        if (!StringUtils.isEmpty(gender)) {
            query.setString("gender", gender);
        }
        if (addressNotNull) {
            query.setParameterList("homeAddressIdList", homeAddressIdList);
        }
        if (!StringUtils.isEmpty(districtList)) {
            query.setParameterList("locationList", locationList);
        }
        if (!StringUtils.isEmpty(searchRegisterTimeStart)) {
            query.setDate("searchRegisterTimeStart", searchRegisterTimeStart);
        }
        if (!StringUtils.isEmpty(searchRegisterTimeEnd)) {
            query.setDate("searchRegisterTimeEnd", searchRegisterTimeEnd);
        }
        return ((Long)query.list().get(0)).intValue();
    }

    private List<String> stringToList(String districtList) {
        List<String> locationList = null;
        if (!StringUtils.isEmpty(districtList)) {
            String[] arr = districtList.split(",");
            locationList = Arrays.asList(arr);
        }
        return locationList;
    }
}