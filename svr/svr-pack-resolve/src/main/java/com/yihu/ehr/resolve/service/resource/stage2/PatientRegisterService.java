package com.yihu.ehr.resolve.service.resource.stage2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.model.patient.MDemographicInfo;
import com.yihu.ehr.resolve.feign.PatientInfoClient;
import com.yihu.ehr.resolve.model.stage2.ResourceBucket;
import com.yihu.ehr.resolve.util.PackResolveLogger;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;

/**
 * Service - 居民信息注册
 * Created by progr1mmer on 2017/12/10.
 */
@Service
public class PatientRegisterService {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PatientInfoClient patientInfoClient;

    public void checkPatient(ResourceBucket resourceBucket, String packId) throws JsonProcessingException, ParseException {
        //获取注册信息
        String idCardNo = resourceBucket.getDemographicId() == null ? "":resourceBucket.getDemographicId().toString().trim();
        if(!idCardNo.equals("")) {
            boolean isRegistered = patientInfoClient.isRegistered(idCardNo);
            if (!isRegistered) {
                MDemographicInfo demoInfo = new MDemographicInfo();
                demoInfo.setIdCardNo(idCardNo);
                String name = resourceBucket.getPatientName() == null ? "":resourceBucket.getPatientName().toString();
                demoInfo.setName(name);
                String telephoneNo = resourceBucket.getMasterRecord().getResourceValue("EHR_000003") == null ? "":resourceBucket.getMasterRecord().getResourceValue("EHR_000003").toString();
                demoInfo.setTelephoneNo("{\"联系电话\":\"" + telephoneNo + "\"}");
                String email = resourceBucket.getMasterRecord().getResourceValue("EHR_000008") == null ? "":resourceBucket.getMasterRecord().getResourceValue("EHR_000008").toString();
                demoInfo.setEmail(email);
                String birthPlace = resourceBucket.getMasterRecord().getResourceValue("EHR_000013") == null ? "":resourceBucket.getMasterRecord().getResourceValue("EHR_000013").toString();
                demoInfo.setBirthPlace(birthPlace);
                String martialStatus = resourceBucket.getMasterRecord().getResourceValue("EHR_000014") == null ? "":resourceBucket.getMasterRecord().getResourceValue("EHR_000014").toString();
                demoInfo.setMartialStatus(martialStatus);
                String nativePlace = resourceBucket.getMasterRecord().getResourceValue("EHR_000015") == null ? "":resourceBucket.getMasterRecord().getResourceValue("EHR_000015").toString();
                demoInfo.setNativePlace(nativePlace);
                String nation = resourceBucket.getMasterRecord().getResourceValue("EHR_000016") ==  null ? "":resourceBucket.getMasterRecord().getResourceValue("EHR_000016").toString();
                demoInfo.setNation(nation);
                String gender = resourceBucket.getMasterRecord().getResourceValue("EHR_000019") == null ? "":resourceBucket.getMasterRecord().getResourceValue("EHR_000019").toString();
                demoInfo.setGender(gender);
                Date birthday = resourceBucket.getMasterRecord().getResourceValue("EHR_000320") == null ? null:DateTimeUtil.simpleDateParse(resourceBucket.getMasterRecord().getResourceValue("EHR_000320"));
                demoInfo.setBirthday(birthday);
                String homeAddress = resourceBucket.getMasterRecord().getResourceValue("EHR_001227") == null ? "":resourceBucket.getMasterRecord().getResourceValue("EHR_001227").toString();
                demoInfo.setHomeAddress(homeAddress);
                //注册
                MDemographicInfo mDemographicInfo = patientInfoClient.registerPatient(objectMapper.writeValueAsString(demoInfo));
                if(null == mDemographicInfo.getRegisterTime()) {
                    PackResolveLogger.warn("档案包:" + packId + ",关联居民:" + idCardNo + ",注册失败!");
                }
            }
        }else {
            PackResolveLogger.warn("档案包:" + packId + ",身份证号码为空!");
        }
    }

}
