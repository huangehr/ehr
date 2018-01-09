package com.yihu.ehr.resolve.service.resource.stage2;

import com.yihu.ehr.entity.patient.Demographic;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resolve.dao.PatientDao;
import com.yihu.ehr.resolve.model.stage2.ResourceBucket;
import com.yihu.ehr.resolve.util.PackResolveLogger;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Date;

/**
 * Service - 居民信息注册
 * Created by progr1mmer on 2017/12/10.
 */
@Service
@Transactional
public class PatientService extends BaseJpaService<Demographic, PatientDao>{

    @Autowired
    private PatientDao patientDao;

    public void checkPatient(ResourceBucket resourceBucket, String packId) throws Exception {
        //获取注册信息
        String idCardNo = resourceBucket.getDemographicId() == null ? "":resourceBucket.getDemographicId().toString().trim();
        if(!idCardNo.equals("")) {
            boolean isRegistered = isExists(idCardNo);
            if (!isRegistered) {
                Demographic demographicInfo = new Demographic();
                demographicInfo.setIdCardNo(idCardNo);
                String name = resourceBucket.getPatientName() == null ? "":resourceBucket.getPatientName().toString();
                demographicInfo.setName(name);
                String telephoneNo = resourceBucket.getMasterRecord().getResourceValue("EHR_000003") == null ? "":resourceBucket.getMasterRecord().getResourceValue("EHR_000003").toString();
                demographicInfo.setTelephoneNo("{\"联系电话\":\"" + telephoneNo + "\"}");
                String email = resourceBucket.getMasterRecord().getResourceValue("EHR_000008") == null ? "":resourceBucket.getMasterRecord().getResourceValue("EHR_000008").toString();
                demographicInfo.setEmail(email);
                String birthPlace = resourceBucket.getMasterRecord().getResourceValue("EHR_000013") == null ? "":resourceBucket.getMasterRecord().getResourceValue("EHR_000013").toString();
                demographicInfo.setBirthPlace(birthPlace);
                String martialStatus = resourceBucket.getMasterRecord().getResourceValue("EHR_000014") == null ? "":resourceBucket.getMasterRecord().getResourceValue("EHR_000014").toString();
                demographicInfo.setMartialStatus(martialStatus);
                String nativePlace = resourceBucket.getMasterRecord().getResourceValue("EHR_000015") == null ? "":resourceBucket.getMasterRecord().getResourceValue("EHR_000015").toString();
                demographicInfo.setNativePlace(nativePlace);
                String nation = resourceBucket.getMasterRecord().getResourceValue("EHR_000016") ==  null ? "":resourceBucket.getMasterRecord().getResourceValue("EHR_000016").toString();
                demographicInfo.setNation(nation);
                String gender = resourceBucket.getMasterRecord().getResourceValue("EHR_000019") == null ? "":resourceBucket.getMasterRecord().getResourceValue("EHR_000019").toString();
                demographicInfo.setGender(gender);
                Date birthday = resourceBucket.getMasterRecord().getResourceValue("EHR_000320") == null ? null:DateTimeUtil.simpleDateParse(resourceBucket.getMasterRecord().getResourceValue("EHR_000320"));
                demographicInfo.setBirthday(birthday);
                String homeAddress = resourceBucket.getMasterRecord().getResourceValue("EHR_001227") == null ? "":resourceBucket.getMasterRecord().getResourceValue("EHR_001227").toString();
                demographicInfo.setHomeAddress(homeAddress);
                //注册
                Demographic demographicInfo1 = registered(demographicInfo);
                if(null == demographicInfo1.getRegisterTime()) {
                    PackResolveLogger.warn("档案包:" + packId + ",关联居民:" + idCardNo + ",注册失败!");
                }
            }
        }else {
            PackResolveLogger.warn("档案包:" + packId + ",身份证号码为空!");
        }
    }

    private boolean isExists(String idCardNo) {
        Session session = currentSession();
        String sql = "SELECT COUNT(id) FROM demographics WHERE id = :id";
        Query query = session.createSQLQuery(sql);
        query.setFlushMode(FlushMode.COMMIT);
        query.setString("id", idCardNo);
        BigInteger count = (BigInteger) query.uniqueResult();
        return count.compareTo(new BigInteger("0")) > 0;
    }

    private Demographic registered(Demographic demographicInfo) {
        String password = "123456";
        if(demographicInfo.getIdCardNo().length() > 7) {
            password = demographicInfo.getIdCardNo().substring(demographicInfo.getIdCardNo().length()-6,demographicInfo.getIdCardNo().length());
            demographicInfo.setPassword(DigestUtils.md5Hex(password));
        }else {
            demographicInfo.setPassword(DigestUtils.md5Hex(password));
        }
        demographicInfo.setRegisterTime(new Date());
        return patientDao.save(demographicInfo);
    }

}
