package com.yihu.ehr.basic.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.basic.fileresource.service.FileResourceManager;
import com.yihu.ehr.basic.fzopen.service.OpenService;
import com.yihu.ehr.basic.org.dao.OrganizationRepository;
import com.yihu.ehr.basic.org.model.Organization;
import com.yihu.ehr.basic.org.service.OrgService;
import com.yihu.ehr.basic.patient.dao.XDemographicInfoRepository;
import com.yihu.ehr.basic.user.dao.XDoctorRepository;
import com.yihu.ehr.basic.user.dao.XUserRepository;
import com.yihu.ehr.basic.user.entity.Doctors;
import com.yihu.ehr.basic.user.entity.User;
import com.yihu.ehr.entity.patient.DemographicInfo;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.util.datetime.DateUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 医生管理接口实现类.
 * 2017-02-04 add by hzp
 */
@Service
@Transactional
public class DoctorService extends BaseJpaService<Doctors, XDoctorRepository> {

    Logger logger = LoggerFactory.getLogger(DoctorService.class);

    @Value("${fast-dfs.public-server}")
    private String dfsPublicUrl;

    @Autowired
    private XUserRepository userRepository;
    @Autowired
    private XDoctorRepository doctorRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private XDemographicInfoRepository demographicInfoRepository;
    @Autowired
    private FileResourceManager fileResourceManager;
    @Autowired
    private OrgService orgService;
    @Autowired
    private OpenService fzOpenService;

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
                    //密码默认12345678
                    String password = "12345678";
                    //有身份证 默认身份证后8位
                    String number = user.getIdCardNo();
                    if(!StringUtils.isEmpty(number))
                    {
                        password = number.substring(number.length()-8);
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
    public String addDoctorBatch(List<Map<String, Object>> doctorLs) {
        String header = "INSERT INTO doctors(code, name, sex, org_code, org_id, org_full_name, dept_name, skill, work_portal, email, phone, jxzc, lczc, xlzc, xzzc, introduction, id_card_no, insert_time, office_tel, status, role_type, job_type, job_level, job_scope, job_state, register_flag) VALUES \n";
        StringBuilder sql = new StringBuilder(header) ;
        Map<String, Object> map;
        SQLQuery query;
        int total = 0;
        DemographicInfo demographicInfo =null;
        for(int i=1; i<=doctorLs.size(); i++){
            map = doctorLs.get(i-1);
            sql.append("('"+ null2Space(map.get("code")) +"'");
            sql.append(",'"+ map.get("name") +"'");
            sql.append(",'"+ map.get("sex") +"'");
            sql.append(",'"+ map.get("orgCode") +"'");
            Organization org= getOrg(map.get("orgCode").toString());
            sql.append(",'"+ org.getId() +"'");
            sql.append(",'"+ map.get("orgFullName") +"'");
            sql.append(",'"+ map.get("orgDeptName") +"'");
            sql.append(",'"+ map.get("skill") +"'");
            sql.append(",'"+ map.get("workPortal") +"'");
            sql.append(",'"+ null2Space(map.get("email")) +"'");
            sql.append(",'"+ null2Space(map.get("phone")) +"'");
            sql.append(",'"+ map.get("jxzc") +"'");
            sql.append(",'"+ map.get("lczc") +"'");
            sql.append(",'"+ map.get("xlzc") +"'");
            sql.append(",'"+ map.get("xzzc") +"'");
            sql.append(",'"+ map.get("introduction") +"'");
            sql.append(",'"+ map.get("idCardNo") +"'");
            sql.append(",'"+ DateUtil.getNowDateTime()+"'");
            sql.append(",'"+ map.get("officeTel") +"'");
            sql.append(",'1'");
            sql.append(",'"+ map.get("roleType") +"'");
            sql.append(",'"+ map.get("jobType") +"'");
            sql.append(",'"+ map.get("jobLevel") +"'");
            sql.append(",'"+ map.get("jobScope") +"'");
            sql.append(",'"+ map.get("jobState") +"'");
            sql.append(",'"+ map.get("registerFlag") +"'");
            sql.append(")\n");

            if(i%100==0 || i == doctorLs.size()){
                query = currentSession().createSQLQuery(sql.toString());
                total += query.executeUpdate();
                sql = new StringBuilder(header) ;
            } else {
                sql.append(",");
            }

            //创建居民
            demographicInfo =new DemographicInfo();
            if(null!=map .get("idCardNo")&&StringUtils.isNotEmpty(map .get("idCardNo").toString())&&map .get("idCardNo").toString().length()>9){
                String idCardNo=map .get("idCardNo").toString();
                String defaultPassword=idCardNo.substring(idCardNo.length()-8);
                demographicInfo.setPassword(DigestUtils.md5Hex(defaultPassword));
            }else{
                demographicInfo.setPassword(DigestUtils.md5Hex("12345678"));
            }
            demographicInfo.setRegisterTime(new Date());
            demographicInfo.setIdCardNo(String.valueOf(map .get("idCardNo")));
            demographicInfo.setName(String.valueOf(map .get("name")));
            demographicInfo.setTelephoneNo("{\"联系电话\":\""+String.valueOf(map .get("phone"))+"\"}");
            demographicInfo.setGender(String.valueOf(map .get("sex")));
            demographicInfoRepository.save(demographicInfo);
        }

        Map<String, Object> phoneMap;
        StringBuffer phonesStr = new StringBuffer();
        for(int i=1; i<=doctorLs.size(); i++) {
            phoneMap = doctorLs.get(i - 1);
            phonesStr.append("\""+phoneMap.get("phone")+"\",");
        }

        return phonesStr.toString();
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
    /**
     * 根据idCardNo获取医生接口.
     * @param idCardNo
     */
    public Doctors getByIdCardNo(String idCardNo) {
        return doctorRepository.findByIdCardNo(idCardNo);
    }

    /**
     * 查询电话号码是否已存在， 返回已存在电话号码
     */
    public List idCardNosExist(String[] idCardNos)
    {
        String sql = "SELECT id_card_no FROM doctors WHERE id_card_no in(:idCardNos)";
        SQLQuery sqlQuery = currentSession().createSQLQuery(sql);
        sqlQuery.setParameterList("idCardNos", idCardNos);
        return sqlQuery.list();
    }

    public Organization getOrg(String orgCode) {
        List<Organization> list =  organizationRepository.findOrgByCode(orgCode);
        if (list.size()>0){
            return list.get(0);
        }else {
            return null;
        }
    }

    /**
     * 机构资源授权获取
     */
    public  List<Object>  getStatisticsDoctorsByRoleType(String roleType)
    {
        String sql =
                "SELECT count(1),omr.org_id,omr.org_name " +
                        "FROM doctors d " +
                        "LEFT JOIN users u " +
                        "ON d.id=u.doctor_id " +
                        "JOIN org_member_relation omr " +
                        "ON u.id=omr.user_id " +
                        "WHERE d.role_type=:roleType and omr.org_id is not null and omr.org_name is not null " +
                        " GROUP BY omr.org_id,omr.org_name";

        SQLQuery query = currentSession().createSQLQuery(sql);
        query.setParameter("roleType", roleType);
        List<Object> list=query.list();
        return list;
    }

    public int getStatisticsCityDoctorByRoleType(String roleType) {
        return doctorRepository.getStatisticsCityDoctorByRoleType(roleType);
    }

    public List<Doctors> searchDoctors(String filter, String[] orgCode, int page, int size) {
        Session s = currentSession();
        String hql = "SELECT d from Doctors d WHERE d.id IN(SELECT u.doctorId from User u where u.id in(" +
                "SELECT DISTINCT(omr.userId) from OrgMemberRelation omr where omr.orgId in(" +
                "SELECT id from Organization o where o.orgCode in(:orgCode))))";
        if (!StringUtils.isEmpty(filter)) {
            hql += " AND d.name LIKE :filter";
        }
        hql += " order by d.insertTime desc";
        Query q = s.createQuery(hql);
        q.setParameterList("orgCode", orgCode);
        if (!StringUtils.isEmpty(filter)) {
            q.setParameter("filter", "%" + filter + "%");
        }
        q.setMaxResults(size);
        q.setFirstResult((page - 1) * size);
        List<Doctors> list = (List<Doctors>)q.list();
        return list;
    }

    public Long getDoctorsCount(String filter, String[] orgCode) {
        Session s = currentSession();
        String sql = "SELECT count(*) from doctors d WHERE d.id IN(SELECT u.doctor_id from users u where u.id in(" +
                "SELECT DISTINCT(omr.user_id) from org_member_relation omr where omr.org_id in(" +
                "SELECT id from organizations where org_code in(:orgCode))))";
        if (!StringUtils.isEmpty(filter)) {
            sql += " AND d.name LIKE :filter";
        }
        Query q = s.createSQLQuery(sql);
        q.setParameterList("orgCode", orgCode);
        if (!StringUtils.isEmpty(filter)) {
            q.setParameter("filter", "%" + filter + "%");
        }
        BigInteger count = (BigInteger ) q.uniqueResult();
        return count.longValue();
    }

    /**
     * 同步科室医生信息到福州总部
     *
     * @param doctor 医生信息
     * @param orgId 机构ID
     * @param deptName 部门名称
     * @return 总部的科室医生信息
     */
    public Map<String, Object> syncDoctor(Doctors doctor, String orgId, String deptName) throws  Exception{
        String api = "baseinfo.DoctorInfoApi.addDoctorFromMedicalCloud";
        int apiVersion = 1;
        Map<String, Object> apiParams = new HashMap<>();
        Organization org = orgService.getOrgById(orgId);
        apiParams.put("orgID", org.getJkzlOrgId());
        apiParams.put("deptName", deptName);
        apiParams.put("doctorName", doctor.getName());
        // 云平台：0（未知），总部：3（未知）
        String sex = "0".equals(doctor.getSex()) ? "3" : doctor.getSex();
        apiParams.put("sex", sex);
        // 转换共同临床职称
        String lczc = "";
        if ("1".equals(doctor.getLczc())) { // 主任医师
            lczc = "0";
        } else if ("2".equals(doctor.getLczc())) { // 副主任医师
            lczc = "1";
        } else if ("3".equals(doctor.getLczc())) { // 主治医师
            lczc = "2";
        } else if ("4".equals(doctor.getLczc())) { // 医师
            lczc = "4";
        }
        apiParams.put("lczc", lczc);
        apiParams.put("phone", doctor.getPhone());
        apiParams.put("skill", doctor.getSkill());
        apiParams.put("intro", doctor.getIntroduction());
        String photoUrl = "";
        if (!StringUtils.isEmpty(doctor.getPhoto())) {
            photoUrl = dfsPublicUrl + "/" +  fileResourceManager.getStoragePathById(doctor.getPhoto()).replace(":", "/");
        }
        apiParams.put("photoUri", photoUrl);
        String syncResult = fzOpenService.callFzInnerApi(api, apiParams, apiVersion);

        Map<String, Object> syncResultMap = null;
        try {
            syncResultMap = objectMapper.readValue(syncResult, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!"10000".equals(syncResultMap.get("Code").toString())) {
            String message = String.format("同步医生信息到福州总部失败：%s，orgId：%s", syncResultMap.get("Message").toString(), orgId);
            logger.warn(message);
            throw new ApiException(message);
        }

        return syncResultMap;
    }

}