package com.yihu.ehr.resolve.service.resource.stage2;

import com.yihu.ehr.entity.patient.DemographicInfo;
import com.yihu.ehr.profile.family.ResourceCells;
import com.yihu.ehr.resolve.model.stage1.OriginalPackage;
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

    @Autowired
    private ArchiveRelationService archiveRelationService;
    @Autowired
    private PatientService patientService;

    public void identify (ResourceBucket resourceBucket, OriginalPackage originalPackage) throws Exception {
        boolean identify = false;
        String demographicId = UUID.randomUUID().toString();
        if (StringUtils.isEmpty(resourceBucket.getBasicRecord(ResourceCells.DEMOGRAPHIC_ID)) || !pattern.matcher(resourceBucket.getBasicRecord(ResourceCells.DEMOGRAPHIC_ID)).find()) {
            boolean recognition = false;
            if (!StringUtils.isEmpty(resourceBucket.getBasicRecord(ResourceCells.CARD_ID))) {
                List<String> idCardNos = archiveRelationService.findIdCardNoByCardNo(resourceBucket.getBasicRecord(ResourceCells.CARD_ID));
                if (!idCardNos.isEmpty()) {
                    recognition = true;
                    demographicId = idCardNos.get(0);
                }
            }
            if (!recognition) {
                String random = UUID.randomUUID().toString();
                //姓名
                String name = StringUtils.isNotEmpty(resourceBucket.getBasicRecord(ResourceCells.PATIENT_NAME)) ?
                        resourceBucket.getBasicRecord(ResourceCells.PATIENT_NAME) : random;
                //生日
                Date birthday = StringUtils.isNotEmpty(resourceBucket.getMasterRecord().getResourceValue("EHR_000007")) ?
                        DateTimeUtil.simpleDateParse(resourceBucket.getMasterRecord().getResourceValue("EHR_000007")) : new Date();
                //手机号码
                String telephoneNo = StringUtils.isNotEmpty(resourceBucket.getMasterRecord().getResourceValue("EHR_000003")) ?
                        resourceBucket.getMasterRecord().getResourceValue("EHR_000003").toString() : random;
                telephoneNo = "{\"联系电话\":\"" + telephoneNo + "\"}";
                //性别
                String gender = StringUtils.isNotEmpty(resourceBucket.getMasterRecord().getResourceValue("EHR_000019")) ?
                        resourceBucket.getMasterRecord().getResourceValue("EHR_000019").toString() : random;
                //家庭住址
                String homeAddress = StringUtils.isNotEmpty(resourceBucket.getMasterRecord().getResourceValue("EHR_001227")) ?
                        resourceBucket.getMasterRecord().getResourceValue("EHR_001227").toString() : random;
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
            demographicId = resourceBucket.getBasicRecord(ResourceCells.DEMOGRAPHIC_ID);
        }
        if (demographicId.length() == 18) {
            identify = idCardValidator.is18Idcard(demographicId);
        }
        if (demographicId.length() == 15) {
            identify = idCardValidator.is15Idcard(demographicId);
        }
        originalPackage.setIdentifyFlag(identify);
        resourceBucket.insertBasicRecord(ResourceCells.DEMOGRAPHIC_ID, demographicId);
    }

}
