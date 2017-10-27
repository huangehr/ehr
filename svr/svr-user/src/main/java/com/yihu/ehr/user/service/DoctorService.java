package com.yihu.ehr.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.user.dao.XDoctorRepository;
import com.yihu.ehr.user.dao.XUserRepository;
import com.yihu.ehr.user.entity.Doctors;
import com.yihu.ehr.user.entity.User;
import com.yihu.ehr.util.hash.HashUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 医生管理接口实现类.
 * 2017-02-04 add by hzp
 */
@Service
@Transactional
public class DoctorService extends BaseJpaService<Doctors, XDoctorRepository> {

    @Autowired
    XUserRepository userRepository;

    @Autowired
    XDoctorRepository doctorRepository;

    @Autowired
    ObjectMapper objectMapper;

    /**
     * 根据用户ID获取医生信息
     */
    public Doctors getDoctorByUserId(String userId) throws Exception {
        return doctorRepository.findByUserId(userId);
    }

    /**
     * 新增医生，同时新增用户（手机号码当登录账号）
     */
    public Doctors saveDoctor(String json) throws Exception {

        Doctors doctor = objectMapper.readValue(json,Doctors.class);
        if(doctor!=null)
        {
            doctor.setInsertTime(new Date());
            doctorRepository.save(doctor);

            String phone = doctor.getPhone();
            if(!StringUtils.isEmpty(phone))
            {
                //判断用户是否存在
                User user = userRepository.findByLoginCode(phone);
                if(user == null)  //新增用户
                {
                    user = objectMapper.readValue(json,User.class);
                    user.setLoginCode(phone);  //手机号码默认登录账号
                    //密码默认123456
                    String password = "123456";
                    //有身份证 默认身份证后6位
                    String number = user.getIdCardNo();
                    if(!StringUtils.isEmpty(number))
                    {
                        password = number.substring(number.length()-6);
                    }
                    user.setPassword(DigestUtils.md5Hex(password));
                    user.setRealName(doctor.getName());
                    user.setCreateDate(new Date());
                    userRepository.save(user);
                }
            }
        }

        return doctor;
    }

    /**
     * 根据code获取医生接口.
     * @param code
     */
    public Doctors getByCode(String code) {
        return doctorRepository.findByCode(code);
    }

    /**
     * 根据ID获取医生接口.
     * @param doctorId
     */
    public Doctors getDoctor(Long doctorId) {
        Doctors doctors = doctorRepository.findOne(doctorId);
        return doctors;
    }

    /**
     * 删除医生
     * @param doctorId
     */
    public void deleteDoctor(Long doctorId) {
        doctorRepository.delete(doctorId);
    }

    /**
     * 更新医生状态
     * @param doctorId
     * @param status
     */
    public void updDoctorStatus(Long doctorId, String status) {
        Doctors doctors = doctorRepository.findOne(doctorId);
        doctors.setStatus(status);
        doctors.setUpdateTime(new Date());
        doctorRepository.save(doctors);
    }

}