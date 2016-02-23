package com.yihu.ehr.patient.service.demographic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.model.geogrephy.MGeography;
import com.yihu.ehr.model.patient.MDemographicInfo;
import com.yihu.ehr.patient.dao.XDemographicInfoRepository;
import com.yihu.ehr.patient.feign.GeographyClient;
import com.yihu.ehr.util.encode.HashUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

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



    public void save(MDemographicInfo demographicInfoModel) throws JsonProcessingException {
        //地址检查并保存
        DemographicInfo demographicInfo = new DemographicInfo();
        BeanUtils.copyProperties(demographicInfoModel,DemographicInfo.class);
        //出生地
        MGeography birthPlace = demographicInfoModel.getBirthPlace();
        if (!isNullAddress(birthPlace)) {
            String addressId = savaAddress(birthPlace);
            demographicInfo.setBirthPlace(addressId);
        }else{
            demographicInfo.setBirthPlace(null);
        }
        //工作地址
        MGeography workAddress = demographicInfoModel.getWorkAddress();
        if (!isNullAddress(workAddress)) {
            String addressid = savaAddress(workAddress);
            demographicInfo.setWorkAddress(addressid);
        }else{
            demographicInfo.setWorkAddress(null);
        }
        //家庭地址
        MGeography homeAddress = demographicInfoModel.getHomeAddress();
        if (!isNullAddress(homeAddress)) {
            String addressId = savaAddress(homeAddress);
            demographicInfo.setHomeAddress(addressId);
        }else{
            demographicInfo.setHomeAddress(null);
        }
        demographicInfoRepository.save(demographicInfo);
    }


    public boolean savePatient(MDemographicInfo demographicInfoModel) throws Exception{
        save(demographicInfoModel);
        return true;
    }

    public List<DemographicInfo> searchPatient(Map<String, Object> args) {
        Session session = entityManager.unwrap(org.hibernate.Session.class);
        String name = (String) args.get("name");
        String idCardNo = (String) args.get("idCardNo");
        Integer page = (Integer) args.get("page");
        Integer pageSize = (Integer) args.get("pageSize");

        String province = (String) args.get("province");
        String city = (String) args.get("city");
        String district = (String) args.get("district");
        List<String> homeAddressIdList = addressClient.search(province,city,district);
        String hql = "from DemographicInfo where (name like :name or id like :idCardNo)";
        if (!StringUtils.isEmpty(province) && !StringUtils.isEmpty(city) &&!StringUtils.isEmpty(district)) {
            hql += " and homeAddress in (:homeAddressIdList)";
        }
        Query query = session.createQuery(hql);
        query.setString("name", "%" + name + "%");
        query.setString("idCardNo", "%" + idCardNo + "%");

        if (!StringUtils.isEmpty(province) && !StringUtils.isEmpty(city) &&!StringUtils.isEmpty(district)) {
            query.setParameterList("homeAddressIdList", homeAddressIdList);
        }
        query.setMaxResults(pageSize);
        query.setFirstResult((page - 1) * pageSize);
        List<DemographicInfo> demographicInfos = query.list();
        return demographicInfos;
    }

    public Integer searchPatientTotalCount(Map<String, Object> args) {
        Session session = entityManager.unwrap(org.hibernate.Session.class);
        String name = (String) args.get("name");
        String idCardNo = (String) args.get("idCardNo");
        String province = (String) args.get("province");
        String city = (String) args.get("city");
        String district = (String) args.get("district");
        List<String> addressIdList = addressClient.search(province,city,district);
        String hql = "from DemographicInfo where (name like :name or id like :idCardNo)";
        if (!StringUtils.isEmpty(province) && !StringUtils.isEmpty(city) &&!StringUtils.isEmpty(district)) {
            hql += " and homeAddress in (:addressIdList)";
        }
        Query query = session.createQuery(hql);
        query.setString("name", "%" + name + "%");
        query.setString("idCardNo", "%" + idCardNo + "%");
        if (!StringUtils.isEmpty(province) && !StringUtils.isEmpty(city) &&!StringUtils.isEmpty(district)) {
            query.setParameterList("addressIdList", addressIdList);
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