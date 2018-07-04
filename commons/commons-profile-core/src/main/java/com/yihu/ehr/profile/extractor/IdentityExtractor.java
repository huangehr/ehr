package com.yihu.ehr.profile.extractor;

import com.yihu.ehr.profile.family.ResourceCells;
import com.yihu.ehr.profile.model.MetaDataRecord;
import com.yihu.ehr.profile.model.PackageDataSet;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.validate.IdCardValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import sun.util.resources.cldr.bas.CalendarData_bas_CM;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 身份信息提取。
 *
 * @author hzp
 * @version 2.0
 * @created 2017.04.23
 */
@Component
@ConfigurationProperties(prefix = "ehr.pack-extractor.identity")
public class IdentityExtractor extends KeyDataExtractor {

    private static final String IdCardNoDictEntry = "01;02";    // 身份字典项代码：身份证号与护照
    private static final IdCardValidator idCardValidator = new IdCardValidator();

    //包含身份信息的数据集
    private List<String> dataSets = new ArrayList<>();
    @Value("${ehr.pack-extractor.identity.meta-data.id-card-no:id-card-no}")
    private String idCardNo;
    @Value("${ehr.pack-extractor.identity.meta-data.id-card-type:id-card-type}")
    private String idCardType;
    @Value("${ehr.pack-extractor.identity.meta-data.patient-name:patient-name}")
    private String patientName;
    @Value("${ehr.pack-extractor.identity.meta-data.birthday:birthday}")
    private String birthday;


    @Override
    public Map<String,Object> extract(PackageDataSet dataSet) throws Exception {
        Map<String,Object> properties = new HashMap<>();
        String _demographicId = "";
        String _patientName = "";
        String _birthday = "";
        //获取身份证和姓名
        if (dataSets.contains(dataSet.getCode())) {
            for (String key : dataSet.getRecordKeys()) {
                MetaDataRecord record = dataSet.getRecord(key);
                //获取身份证号码
                if (StringUtils.isEmpty(_demographicId)) {
                    String val = record.getMetaData(idCardType);
                    if (StringUtils.isEmpty(val)) {
                        // default as identity card no
                        String val2 = record.getMetaData(idCardNo);
                        if (val2 != null) {
                            _demographicId = val2.trim();
                        }
                    } else {
                        if (IdCardNoDictEntry.contains(val)) {
                            String val2 = record.getMetaData(idCardNo);
                            if (val2 != null) {
                                _demographicId = val2.trim();
                            }
                        }
                    }
                }
                //获取姓名
                if (StringUtils.isEmpty(_patientName)) {
                    String val = record.getMetaData(patientName);
                    if (val != null) {
                        _patientName = val.trim();
                    }
                }
                //获取生日
                if (StringUtils.isEmpty(_birthday)) {
                    String val = record.getMetaData(birthday);
                    if (val != null) {
                        _birthday = val.trim();
                    }
                }
            }
        }
        properties.put(ResourceCells.DEMOGRAPHIC_ID, _demographicId);
        properties.put(ResourceCells.PATIENT_NAME, _patientName);
        properties.put(ResourceCells.PATIENT_AGE, this.getPatientAge(dataSet.getEventTime(), _birthday, _demographicId));
        return properties;
    }

    private static String getPatientAge(Date _eventDate, String birthday, String demographicId) throws Exception {
        if (!StringUtils.isEmpty(birthday)) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date _birthday = dateFormat.parse(birthday.substring(0, birthday.indexOf("T")));
            long time = (_eventDate.getTime() - _birthday.getTime()) / 1000;
            if (time > 31536000) { //一年按365天
                return  (time % 31536000 > 0 ? time / 31536000 + 1 : time / 31536000) + "岁";
            }
            if (time > 2592000) { //一个月按30天
                return (time % 2592000 > 0 ? time / 2592000 + 1 : time / 2592000) + "月";
            }
            long day = (time % 86400 > 0 ? time / 86400 + 1 : time / 86400);
            if (day == 30) { //一旦超过29天则按月显示
                return "1月";
            }
            return day + "天";
        }
        if (!StringUtils.isEmpty(demographicId)) {
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            boolean identify = false;
            if (demographicId.length() == 18) {
                identify = idCardValidator.is18Idcard(demographicId);
            }
            if (demographicId.length() == 15) {
                identify = idCardValidator.is15Idcard(demographicId);
            }
            if (identify) {
                Date _birthday = dateFormat.parse(demographicId.substring(6, 14));
                long time = (_eventDate.getTime() - _birthday.getTime()) / 1000;
                if (time > 31536000) { //一年按365天
                    return  (time % 31536000 > 0 ? time / 31536000 + 1 : time / 31536000) + "岁";
                }
                if (time > 2592000) {  //一个月按30天
                    return (time % 2592000 > 0 ? time / 2592000 + 1 : time / 2592000) + "月";
                }
                long day = (time % 86400 > 0 ? time / 86400 + 1 : time / 86400);
                if (day == 30) { //一旦超过29天则按月显示
                    return "1月";
                }
                return day + "天";
            }
        }
        return "";
    }

    public static void main(String [] args) throws Exception {
        System.out.println(getPatientAge(new Date(), "", ""));
    }

    public List<String> getDataSets() {
        return this.dataSets;
    }
}
