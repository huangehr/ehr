package com.yihu.ehr.resolve.service.resource.stage2;

import com.yihu.ehr.entity.patient.DemographicInfo;
import com.yihu.ehr.resolve.model.stage1.StandardPackage;
import com.yihu.ehr.resolve.model.stage2.ResourceBucket;
import com.yihu.ehr.resolve.service.profile.ArchiveRelationService;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import com.yihu.ehr.util.validate.IdCardValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Created by progr1mmer on 2018/4/3.
 */
@Service
public class IdentifyService {

    private static final IdCardValidator idCardValidator = new IdCardValidator();
    private static final Pattern pattern = Pattern.compile("^[A-Za-z0-9\\-]+$");
    private static final String DEFAULT_VALUE = "default";

    @Autowired
    private ArchiveRelationService archiveRelationService;
    @Autowired
    private PatientService patientService;

    public void identify (ResourceBucket resourceBucket, StandardPackage standardPackage) throws Exception {
        boolean identify = false;
        String demographicId = UUID.randomUUID().toString();
        if (StringUtils.isEmpty(standardPackage.getDemographicId()) || !pattern.matcher(standardPackage.getDemographicId()).find()) {
            boolean recognition = false;
            if (!StringUtils.isEmpty(standardPackage.getCardId())) {
                List<String> idCardNos = archiveRelationService.findIdCardNoByCardNo(standardPackage.getCardId());
                if (!idCardNos.isEmpty()) {
                    recognition = true;
                    demographicId = idCardNos.get(0);
                }
            }
            if (!recognition) {
                //姓名
                String name = StringUtils.isNotEmpty(resourceBucket.getPatientName()) ? resourceBucket.getPatientName() : DEFAULT_VALUE;
                //生日
                Date birthday = resourceBucket.getMasterRecord().getResourceValue("EHR_000007") == null ? new Date() : DateTimeUtil.simpleDateParse(resourceBucket.getMasterRecord().getResourceValue("EHR_000007"));
                //手机号码
                String telephoneNo = resourceBucket.getMasterRecord().getResourceValue("EHR_000003") == null ? DEFAULT_VALUE : resourceBucket.getMasterRecord().getResourceValue("EHR_000003").toString();
                telephoneNo += "{\"联系电话\":\"" + telephoneNo + "\"}";
                //性别
                String gender = resourceBucket.getMasterRecord().getResourceValue("EHR_000019") == null ? DEFAULT_VALUE : resourceBucket.getMasterRecord().getResourceValue("EHR_000019").toString();
                //家庭住址
                String homeAddress = resourceBucket.getMasterRecord().getResourceValue("EHR_001227") == null ?  DEFAULT_VALUE :resourceBucket.getMasterRecord().getResourceValue("EHR_001227").toString();
                List<DemographicInfo> demographicInfoList = patientService.findByNameOrBirthdayOrTelephoneNo(name, birthday, telephoneNo);
                if (!demographicInfoList.isEmpty()) {
                    for (DemographicInfo demographicInfo : demographicInfoList) {
                        int match = 0;
                        if (name.equals(demographicInfo.getName())) {
                            match ++;
                        }
                        if (demographicInfo.getBirthday() != null && birthday.getTime() == demographicInfo.getBirthday().getTime()) {
                            match ++;
                        }
                        if (telephoneNo.equals(demographicInfo.getTelephoneNo())) {
                            match ++;
                        }
                        if (gender.equals(demographicInfo.getGender())) {
                            match ++;
                        }
                        if (homeAddress.equals(demographicInfo.getHomeAddress())) {
                            match ++;
                        }
                        if (match >= 3) {
                            demographicId = demographicInfo.getIdCardNo();
                            break;
                        }
                    }
                }
            }
        } else {
            demographicId = standardPackage.getDemographicId();
        }
        if (demographicId.length() == 18) {
            identify = idCardValidator.is18Idcard(demographicId);
        }
        if (demographicId.length() == 15) {
            identify = idCardValidator.is15Idcard(demographicId);
        }
        standardPackage.setDemographicId(demographicId);
        resourceBucket.setDemographicId(demographicId);
        standardPackage.setIdentifyFlag(identify);
        resourceBucket.setIdentifyFlag(identify);
    }

}
