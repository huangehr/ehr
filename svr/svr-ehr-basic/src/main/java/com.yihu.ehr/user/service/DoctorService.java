package com.yihu.ehr.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.user.dao.XDoctorRepository;
import com.yihu.ehr.user.dao.XUserRepository;
import com.yihu.ehr.user.entity.Doctors;
import com.yihu.ehr.user.entity.User;
import com.yihu.ehr.util.hash.HashUtil;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
                    user.setPassword(HashUtil.hash(password));
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


    /**
     * 查询电话号码是否已存在， 返回已存在电话号码
     */
    public List idExist(String[] phones)
    {
        String sql = "SELECT phone FROM doctors WHERE phone in(:phones)";
        SQLQuery sqlQuery = currentSession().createSQLQuery(sql);
        sqlQuery.setParameterList("phones", phones);
        return sqlQuery.list();
    }
    /**
     * 批量创建医生
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public String addDoctorBatch(List<Map<String, Object>> doctorLs)
    {
        String header = "INSERT INTO doctors(code, name, sex, skill, work_portal, email, phone,jxzc,lczc,xlzc,xzzc,introduction, office_tel, status) VALUES \n";
        StringBuilder sql = new StringBuilder(header) ;
        Map<String, Object> map;
        SQLQuery query;
        int total = 0;
        for(int i=1; i<=doctorLs.size(); i++){
            map = doctorLs.get(i-1);
            sql.append("('"+ null2Space(map .get("code")) +"'");
            sql.append(",'"+ map .get("name") +"'");
            sql.append(",'"+ map .get("sex") +"'");
            sql.append(",'"+ map .get("skill") +"'");
            sql.append(",'"+ map .get("workPortal") +"'");
            sql.append(",'"+ null2Space(map .get("email")) +"'");
            sql.append(",'"+ null2Space(map .get("phone")) +"'");
            sql.append(",'"+ map .get("jxzc") +"'");
            sql.append(",'"+ map .get("lczc") +"'");
            sql.append(",'"+ map .get("xlzc") +"'");
            sql.append(",'"+ map .get("xzzc") +"'");
            sql.append(",'"+ map .get("introduction") +"'");
            sql.append(",'"+ map .get("officeTel") +"','1')\n");

            if(i%100==0 || i == doctorLs.size()){
                query = currentSession().createSQLQuery(sql.toString());
                total += query.executeUpdate();
                sql = new StringBuilder(header) ;
            }else
                sql.append(",");
        }
        Map<String, Object> phoneMap;
        StringBuffer stringBuffer = new StringBuffer();
        for(int i=1; i<=doctorLs.size(); i++) {
            phoneMap = doctorLs.get(i - 1);

            stringBuffer.append("\""+phoneMap.get("phone")+"\",");
        }

        return stringBuffer.toString();
    }
    private Object null2Space(Object o){
        return o==null? "" : o;
    }

    /**
     * 查询电话号码是否已存在， 返回已存在电话号码
     */
    public List getIdByPhone(String[] phones)
    {
        String sql = "SELECT d.* FROM doctors d WHERE phone in(:phones)";
        SQLQuery sqlQuery = currentSession().createSQLQuery(sql);
        sqlQuery.setParameterList("phones", phones);
        return sqlQuery.list();
    }

    /**
     * 查询电话号码是否已存在， 返回已存在邮箱
     */
    public List emailsExistence(String[] emails)
    {
        String sql = "SELECT email FROM doctors WHERE email in(:emails)";
        SQLQuery sqlQuery = currentSession().createSQLQuery(sql);
        sqlQuery.setParameterList("emails", emails);
        return sqlQuery.list();
    }

}