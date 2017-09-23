package com.yihu.ehr.service.resource.stage2.repo;

import com.yihu.ehr.model.patient.MDemographicInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by progr1mmer on 2017/9/22.
 */
@Repository
public class PatientInfoRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean isRegistered(String idCardNo) {
        String sql = "SELECT COUNT(*) FROM demographics WHERE id = '" + idCardNo + "'";
        int count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count > 0;
    }

    public boolean save(MDemographicInfo mDemographicInfo){
        String sql = "INSERT INTO demographics (id, name, birthday, native_place, gender, martial_status, nation, telphone_number, email, achive_date) VALUES (?, ?, ?, ?, ?, ?, ?, ? ,?, ?)";
        int count = jdbcTemplate.update(sql, new Object[]{
                mDemographicInfo.getIdCardNo(),
                mDemographicInfo.getName(),
                mDemographicInfo.getBirthday(),
                mDemographicInfo.getNativePlace(),
                mDemographicInfo.getGender(),
                mDemographicInfo.getMartialStatus(),
                mDemographicInfo.getNation(),
                mDemographicInfo.getTelephoneNo(),
                mDemographicInfo.getEmail(),
                new Date()
        });
        return count > 0;
    }

}
