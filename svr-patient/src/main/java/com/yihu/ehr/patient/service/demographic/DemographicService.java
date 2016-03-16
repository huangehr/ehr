package com.yihu.ehr.patient.service.demographic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.model.geogrephy.MGeography;
import com.yihu.ehr.patient.dao.XDemographicInfoRepository;
import com.yihu.ehr.patient.feign.GeographyClient;
import com.yihu.ehr.util.encode.HashUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Map;

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
        String hql = "from DemographicInfo where 1=1";
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
        return query.list().size();
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
        demInfo.setPassword(HashUtil.hashStr(pwd));
        demographicInfoRepository.save(demInfo);
    }
}